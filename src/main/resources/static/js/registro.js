document.addEventListener('DOMContentLoaded', function () {
    const dniInput = document.getElementById('dni');

    if (dniInput) {
        dniInput.addEventListener('input', function (e) {
            // Remover caracteres no numéricos
            this.value = this.value.replace(/[^0-9]/g, '');

            // Validar longitud máxima
            if (this.value.length > 8) {
                this.value = this.value.slice(0, 8);
                alert('El DNI no puede exceder los 8 dígitos.');
            }
        });
    }
});
