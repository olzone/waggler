#include <pebble.h>
#define ACC_SAMPLES 1

static Window *window;
static TextLayer *text_layer;

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
}

static void up_click_handler(ClickRecognizerRef recognizer, void *context) {
}

static void down_click_handler(ClickRecognizerRef recognizer, void *context) {
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

#define JS_READY 7
bool js_ready = false;

static void inbox_received(DictionaryIterator* iterator, void* context) {
    APP_LOG(APP_LOG_LEVEL_INFO, "inbox received");
    Tuple *tuple = dict_read_first(iterator);
    while (tuple) {
      switch (tuple->key) {
        case JS_READY:
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

static void acc_handler(AccelData* data, uint32_t samples) {
    const char* type = "ACC_DATA";
    static char d_buff[128];
    snprintf(d_buff, sizeof(d_buff), "%d %d %d", data[0].x, data[0].y, data[0].z);
    text_layer_set_text(text_layer, d_buff);
    
    for (uint32_t i = 0; i < samples; i++) {
        APP_LOG(APP_LOG_LEVEL_INFO, d_buff);
    /*    DictionaryIterator* iter;
        app_message_outbox_begin(&iter); 
        dict_write_cstring(iter, 0, type);
        dict_write_int(iter, 1, &data[i].x, sizeof(int), true);
        dict_write_int(iter, 2, &data[i].y, sizeof(int), true);
        dict_write_int(iter, 3, &data[i].z, sizeof(int), true);
        app_message_outbox_send();*/
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
    accel_service_set_sampling_rate(ACCEL_SAMPLING_10HZ);

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
