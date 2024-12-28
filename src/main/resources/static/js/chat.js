var stompClient = null;
var messages = [];  // Store chat messages globally
var username = username || 'Guest'; // Set default username if undefined

// Connect to WebSocket and load last 10 messages
function connect() {
    var socket = new SockJS('/chat'); // Connect to WebSocket endpoint
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // Subscribe to messages topic for real-time updates
        stompClient.subscribe('/topic/messages', function(messageOutput) {
            const newMessage = JSON.parse(messageOutput.body);
            appendMessage(newMessage); // Append new message
        });

        // Load previous 10 messages from the server (fetch from Redis)
        fetchMessages();
    }, function(error) {
        console.error('WebSocket connection error:', error);
    });
}

// Send a message
window.sendMessage = function() {
    var messageContent = document.getElementById('message-input').value.trim();

    if (messageContent) {
        const message = {
            sender: username,  // Use dynamic username
            content: messageContent,
            timestamp: new Date().toISOString() // ISO format timestamp
        };

        console.log('Sending message:', message);
        stompClient.send("/app/chat", {}, JSON.stringify(message)); // Send via WebSocket
        document.getElementById('message-input').value = ''; // Clear input field
    }
};

// Append a new message to the chat window
function appendMessage(message) {
    // Add message to the array
    messages.push(message);

    // Keep only the last 10 messages
    if (messages.length > 10) {
        messages.shift(); // Remove oldest message if more than 10
    }

    // Render the updated messages
    renderMessages();
}

// Render all messages in the chat window
function renderMessages() {
    const chatMessages = document.getElementById('chat-messages');
    chatMessages.innerHTML = ''; // Clear current messages

    messages.forEach(msg => {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message');

        const formattedTimestamp = new Date(msg.timestamp).toLocaleString();
        messageElement.innerHTML = `
            <strong>${msg.sender}</strong> <span class="timestamp">[${formattedTimestamp}]</span>: ${msg.content}
        `;

        chatMessages.appendChild(messageElement);
    });

    scrollToBottom();
}

// Fetch the last 10 messages from the server
fetch('/api/messages/get?count=10')
    .then(response => {
        if (!response.ok) throw new Error('Failed to fetch messages');
        return response.json();
    })
    .then(fetchedMessages => {
        console.log('Fetched messages:', fetchedMessages);  // Log fetched messages
        messages = fetchedMessages;
        renderMessages();  // Render messages
    })
    .catch(error => {
        console.error('Error fetching messages:', error);  // Log any errors
        alert('Could not load previous messages. Please refresh or try again later.');
    });


// Session timeout check
function checkSession() {
    fetch('/api/session/check') // Check if session is valid
        .then(response => {
            if (!response.ok) {
                alert('Session expired! Please log in again.');
                window.location.href = '/login'; // Redirect on session expiration
            }
        })
        .catch(() => {
            alert('Session expired! Please log in again.');
            window.location.href = '/login';
        });
}

// Scroll chat to the bottom
function scrollToBottom() {
    const chatMessages = document.getElementById('chat-messages');
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    connect(); // Establish WebSocket connection
    setInterval(checkSession, 60000); // Session check every minute
});

// Ensure WebSocket disconnects on page unload
window.onbeforeunload = function() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
};

// Ensure scroll is at the bottom on page load
window.onload = scrollToBottom;
