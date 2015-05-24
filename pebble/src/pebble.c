#include <pebble.h>
#define ACC_SAMPLES 3
#define ACC_BATCH_SIZE 5
#define SAMPLES (ACC_SAMPLES * ACC_BATCH_SIZE)
#define JS_READY 7

static Window *window;
static TextLayer *text_layer;
static bool js_ready = false;
static int recording = false;
static bool ended = false;

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
    static const char* type = "SELECT";
    if (js_ready) {
        DictionaryIterator* iter;
        app_message_outbox_begin(&iter); 
        dict_write_cstring(iter, 0, type);
        app_message_outbox_send();
    }
}

static void up_click_handler(ClickRecognizerRef recognizer, void *context) {
    if (js_ready) {
        ended = true;
    }
}

static void down_click_handler(ClickRecognizerRef recognizer, void *context) {
    static const char* type = "START";
    if (js_ready) {
        DictionaryIterator* iter;
        app_message_outbox_begin(&iter); 
        dict_write_cstring(iter, 0, type);
        app_message_outbox_send();
        recording = true;
    }
}

static void click_config_provider(void *context) {
    window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
    window_single_click_subscribe(BUTTON_ID_UP, up_click_handler);
    window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
}

static void window_load(Window *window) {
    Layer *window_layer = window_get_root_layer(window);
    GRect bounds = layer_get_bounds(window_layer);

    text_layer = text_layer_create((GRect) { .origin = { 0, 72 }, .size = { bounds.size.w, 20 } });
    text_layer_set_text(text_layer, "Shake it off!");
    text_layer_set_text_alignment(text_layer, GTextAlignmentCenter);
    layer_add_child(window_layer, text_layer_get_layer(text_layer));
}

static void window_unload(Window *window) {
    text_layer_destroy(text_layer);
}

static void inbox_received(DictionaryIterator* iterator, void* context) {
    APP_LOG(APP_LOG_LEVEL_INFO, "inbox received");
    Tuple *tuple = dict_read_first(iterator);
    while (tuple) {
      switch (tuple->key) {
        case JS_READY:
          js_ready = true;
          break;
        case 1:
          APP_LOG(APP_LOG_LEVEL_INFO, tuple->value->cstring);
          break;
      }
      tuple = dict_read_next(iterator);
    }
}
static void inbox_dropped(AppMessageResult reason, void* context) {
    APP_LOG(APP_LOG_LEVEL_INFO, "inbox dropped");
}

static void outbox_failed(DictionaryIterator* iterator, AppMessageResult result, void* context) {
    char buff[2];
    buff[0] = (int)result + '0';
    buff[1] = 0;
    APP_LOG(APP_LOG_LEVEL_INFO, "outbox failed");
    APP_LOG(APP_LOG_LEVEL_INFO, buff);
}

static void outbox_sent(DictionaryIterator* iterator, void* context) {
    APP_LOG(APP_LOG_LEVEL_INFO, "outbox sent");
}

static void flush_acc_batch(AccelData* batch, uint32_t dict_size) {
    static const char* type = "ACC_DATA";
    DictionaryIterator* iter;
    app_message_outbox_begin(&iter); 
    dict_write_cstring(iter, 0, type);
    for (uint32_t i = 0; i < dict_size; i++) {
        dict_write_int(iter, i * 3 + 1, &batch[i].x, sizeof(short int), true);
        dict_write_int(iter, i * 3 + 2, &batch[i].y, sizeof(short int), true);
        dict_write_int(iter, i * 3 + 3, &batch[i].z, sizeof(short int), true);
    }
    app_message_outbox_send();
}

static void acc_handler(AccelData* data, uint32_t samples) {
    static char d_buff[128];
    static AccelData batch[SAMPLES];
    static const char* end = "END";
    static uint32_t dict_size = 0;
    static int reads = 0;
    reads++;
    snprintf(d_buff, sizeof(d_buff), "[%d %d %d] %d", data[0].x, data[0].y, data[0].z, reads);
    text_layer_set_text(text_layer, d_buff);
    
    if (js_ready && recording) {
        for (uint32_t i = 0; i < samples; i++) {
            APP_LOG(APP_LOG_LEVEL_INFO, d_buff);
            batch[dict_size++] = data[i];
        }
    }
    if (dict_size >= SAMPLES ||
            ended == true) {
        if (dict_size > 0) {
            flush_acc_batch(batch, dict_size);
        }
        if (ended) {
            DictionaryIterator* iter;
            app_message_outbox_begin(&iter); 
            dict_write_cstring(iter, 0, end);
            app_message_outbox_send();
            recording = false;
            ended = false;
        }
        dict_size = 0;
    }
}

static void init(void) {
    // app msgs
    app_message_register_inbox_received(inbox_received);
    app_message_register_inbox_dropped(inbox_dropped);
    app_message_register_outbox_failed(outbox_failed);
    app_message_register_outbox_sent(outbox_sent);
    app_message_open(app_message_inbox_size_maximum(), app_message_outbox_size_maximum());

    // acc
    accel_data_service_subscribe(ACC_SAMPLES, acc_handler);
    accel_service_set_sampling_rate(ACCEL_SAMPLING_25HZ);

    // window
    window = window_create();
    window_set_click_config_provider(window, click_config_provider);
    window_set_window_handlers(window, (WindowHandlers) {
      .load = window_load,
      .unload = window_unload,
    });
    const bool animated = true;
    window_stack_push(window, animated);
}

static void deinit(void) {
    window_destroy(window);
}

int main(void) {
    init();

    APP_LOG(APP_LOG_LEVEL_DEBUG, "Done initializing, pushed window: %p", window);

    app_event_loop();
    deinit();
}
