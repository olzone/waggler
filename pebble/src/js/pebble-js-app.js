Pebble.addEventListener("ready",
    function(e) {
        console.log("Hello world! - Sent from your javascript application.");
/*
        ws = new WebSocket('ws://192.168.43.107:8000');
        ws.onopen = function(evt) {
            console.log('asdf');
            ws.send('sent app msg');
        }

        ws.onmessage(function (evt) {
//            json = JSON.parse(evt.data);
            console.log('received:' + evt.data);
        });
*/
        var transactionId = Pebble.sendAppMessage( { '0': 42, '1': 'String value' },
          function(e) {
            console.log('Successfully delivered message with transactionId=' + e.data.transactionId);
          },
          function(e) {
            console.log('Unable to deliver message with transactionId='
              + e.data.transactionId
              + ' Error is: ' + e.error.message);
          }
        );

    }
);

Pebble.addEventListener('appmessage',
  function(e) {
    console.log('Received message: ' + JSON.stringify(e.payload));
  }
);
