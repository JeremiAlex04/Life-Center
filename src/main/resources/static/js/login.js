document.addEventListener('DOMContentLoaded', function() {
    const togglePassword = document.querySelector('#togglePassword');
    const password = document.querySelector('#password');

    if (togglePassword && password) {
        togglePassword.addEventListener('click', function (e) {
            // toggle the type attribute
            const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
            password.setAttribute('type', type);
            
            // toggle the eye slash icon
            const icon = this.querySelector('i');
            if (icon) {
                icon.classList.toggle('bi-eye-slash-fill');
                icon.classList.toggle('bi-eye-fill');
            }
        });
    }
});
