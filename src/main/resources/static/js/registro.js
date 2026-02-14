document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('registroForm');
    const dniInput = document.getElementById('dni');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const dniHelp = document.getElementById('dniHelp');
    const passwordHelp = document.getElementById('passwordHelp');

    function validarDni() {
        if (!dniInput) return true;

        dniInput.value = dniInput.value.replace(/[^0-9]/g, '').slice(0, 8);
        const valido = /^\d{8}$/.test(dniInput.value);

        if (dniHelp) {
            dniHelp.textContent = valido
                ? 'DNI valido.'
                : 'El DNI debe tener exactamente 8 digitos numericos.';
            dniHelp.className = valido ? 'form-text text-success' : 'form-text text-danger';
        }

        return valido;
    }

    function validarPasswords() {
        if (!passwordInput || !confirmPasswordInput) return true;

        const password = passwordInput.value || '';
        const confirm = confirmPasswordInput.value || '';

        if (password.length < 8) {
            passwordHelp.textContent = 'La contrasena debe tener al menos 8 caracteres.';
            passwordHelp.className = 'form-text text-danger';
            return false;
        }

        if (password !== confirm) {
            passwordHelp.textContent = 'Las contrasenas no coinciden.';
            passwordHelp.className = 'form-text text-danger';
            return false;
        }

        passwordHelp.textContent = 'Contrasena valida.';
        passwordHelp.className = 'form-text text-success';
        return true;
    }

    if (dniInput) {
        dniInput.addEventListener('input', validarDni);
        dniInput.addEventListener('blur', validarDni);
    }

    if (passwordInput && confirmPasswordInput) {
        passwordInput.addEventListener('input', validarPasswords);
        confirmPasswordInput.addEventListener('input', validarPasswords);
    }

    if (form) {
        form.addEventListener('submit', function (event) {
            const dniOk = validarDni();
            const passOk = validarPasswords();

            if (!dniOk || !passOk) {
                event.preventDefault();
                event.stopPropagation();
            }
        });
    }
});
