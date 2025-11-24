document.addEventListener('DOMContentLoaded', function () {
    const especialidadSelect = document.getElementById('especialidad');
    const medicoSelect = document.getElementById('medicoSelect');

    especialidadSelect.addEventListener('change', function () {
        const selectedEspecialidad = this.value;
        medicoSelect.innerHTML = '<option value="" disabled selected>Cargando médicos...</option>'; // Limpiar y poner mensaje

        if (selectedEspecialidad) {
            fetch(`/citas/api/medicos-por-especialidad?especialidad=${selectedEspecialidad}`)
                .then(response => response.json())
                .then(medicos => {
                    medicoSelect.innerHTML = '<option value="" disabled selected>Seleccione un médico</option>'; // Limpiar antes de añadir nuevos
                    if (medicos.length > 0) {
                        medicos.forEach(medico => {
                            const option = document.createElement('option');
                            option.value = medico.idMedico;
                            option.textContent = `${medico.nombres} ${medico.apellidos} - ${medico.especialidad}`;
                            medicoSelect.appendChild(option);
                        });
                    } else {
                        medicoSelect.innerHTML = '<option value="" disabled selected>No hay médicos disponibles para esta especialidad</option>';
                    }
                })
                .catch(error => {
                    console.error('Error al obtener médicos:', error);
                    medicoSelect.innerHTML = '<option value="" disabled selected>Error al cargar médicos</option>';
                });
        } else {
            medicoSelect.innerHTML = '<option value="" disabled selected>Seleccione una especialidad primero</option>';
        }
    });
});


