var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    console.log('Connected to WebSocket.');

    stompClient.subscribe('/topic/messages', function(response) {
        var message = response.body;
        // Handle the received message
        console.log('Received: ' + message);
    });
});

function sendMessage(message) {
    stompClient.send('/app/message', {}, message);
}
