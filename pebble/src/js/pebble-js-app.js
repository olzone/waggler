Pebble.addEventListener("ready",
    function(e) {
        console.log("Hello world! - Sent from your javascript application.");
        ws = null;
        sendMsg({'7':0});
        msg_queue = [];
    }
);

function sendMsg(data) {
    return Pebble.sendAppMessage( data,
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

function isFlushNeeded() {
    for (var i = 0; i < msg_queue.length; i++) {
        if (msg_queue[i]['dummy'] == 'END' ||
                msg_queue[i]['dummy'] == 'START') {
            return true;
        }
    }
    return false;
}

Pebble.addEventListener('appmessage',
  function(e) {
//    console.log('Received message: ' + JSON.stringify(e.payload));
    
    if (ws === null || ws.readyState > WebSocket.OPEN) {
        //echo to 107
        //krol to 38
        ws = new WebSocket('ws://192.168.43.107:8000');
    }
    
    console.log(JSON.stringify(e.payload));
    if (e.payload['dummy'] == 'ACC_DATA') {
        var points = {}
        for (var key in e.payload) {
            if (key != 'dummy') {
                var i_key = parseInt(key);
                var index = Math.floor((i_key - 1) / 3);
                if (!(index in points)) {
                    points[index] = [];
                }
                points[index][(i_key - 1) % 3] = e.payload[key];
            }
        }
        console.log(JSON.stringify(points));
        for (var i in points) {
            var obj = {
                'X' : points[i][0],
                'Y' : points[i][1],
                'Z' : points[i][2],
                'dummy' : 'ACC_DATA',
            };
            console.log(JSON.stringify(obj));
            msg_queue.push(obj);
        }
    } else {
        msg_queue.push(e.payload);
    }
    //console.log('msgs:' + JSON.stringify(msg_queue));
    if ((msg_queue.length > 10 || isFlushNeeded()) && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify(msg_queue));
        msg_queue = [];
    }
  }
);
