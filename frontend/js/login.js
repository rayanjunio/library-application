document.addEventListener("DOMContentLoaded", function () {
document.getElementById('loginForm').addEventListener('submit', async(event) => {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const res = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({email, password}),
    });

    if (res.ok) {
        const data = await res.json();
        localStorage.setItem('token', data.token);
        console.log('logged');
    } else {
        const data = await res.json();
        alert(data.message);
    }
});
});