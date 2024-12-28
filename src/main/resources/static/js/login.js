window.onload = function() {
    const form = document.querySelector('form');
    form.onsubmit = function() {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        if (!username || !password) {
            alert('Both fields are required!');
            return false;
        }
    };
};
