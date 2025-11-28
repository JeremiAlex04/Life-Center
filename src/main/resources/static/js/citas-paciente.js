document.addEventListener('DOMContentLoaded', function() {
    const medicoSelect = document.getElementById('medico');
    const fechaInput = document.getElementById('fecha');
    const slotsContainer = document.getElementById('slotsContainer');
    const loadingSlots = document.getElementById('loadingSlots');
    const horaInput = document.getElementById('hora');
    const btnConfirmar = document.getElementById('btnConfirmar');
    const especialidadSelect = document.getElementById('especialidad');
    const horarioHelp = document.getElementById('horarioHelp');
    const rangoHorario = document.getElementById('rangoHorario');

    // Establecer fecha mínima como hoy
    const hoy = new Date().toISOString().split('T')[0];
    fechaInput.setAttribute('min', hoy);

    function cargarHorarios() {
        const medicoId = medicoSelect.value;
        const fecha = fechaInput.value;

        if (!medicoId || !fecha) {
            slotsContainer.innerHTML = '<div class="text-muted fst-italic">Seleccione un médico y una fecha para ver los horarios disponibles.</div>';
            return;
        }

        // Limpiar y mostrar loading
        slotsContainer.innerHTML = '';
        loadingSlots.style.display = 'block';
        horaInput.value = '';
        btnConfirmar.disabled = true;

        fetch(`/api/horarios/disponibles?medicoId=${medicoId}&fecha=${fecha}`)
            .then(response => response.json())
            .then(horarios => {
                loadingSlots.style.display = 'none';
                
                if (horarios.length === 0) {
                    slotsContainer.innerHTML = '<div class="alert alert-warning w-100"><i class="bi bi-exclamation-circle"></i> No hay horarios disponibles para esta fecha.</div>';
                    return;
                }

                horarios.forEach(horario => {
                    const btn = document.createElement('button');
                    btn.type = 'button';
                    btn.className = 'btn btn-outline-primary slot-btn mb-2';
                    // Formatear hora HH:mm:ss a HH:mm
                    const horaCorta = horario.horaInicio.substring(0, 5);
                    btn.textContent = horaCorta;
                    
                    btn.onclick = function() {
                        // Remover clase active de todos
                        document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('active'));
                        // Activar este
                        btn.classList.add('active');
                        // Guardar valor
                        horaInput.value = horario.horaInicio;
                        btnConfirmar.disabled = false;
                    };
                    
                    slotsContainer.appendChild(btn);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                loadingSlots.style.display = 'none';
                slotsContainer.innerHTML = '<div class="alert alert-danger w-100">Error al cargar horarios. Intente nuevamente.</div>';
            });
    }

    medicoSelect.addEventListener('change', function() {
        actualizarHorario();
        cargarHorarios();
    });
    
    fechaInput.addEventListener('change', cargarHorarios);
    
    // Evento para filtrar médicos
    especialidadSelect.addEventListener('change', function() {
        filtrarMedicos();
    });

    function filtrarMedicos() {
        const selectedEspecialidad = especialidadSelect.value;
        const options = medicoSelect.options;
        
        medicoSelect.value = "";
        horarioHelp.style.display = 'none';
        slotsContainer.innerHTML = '<div class="text-muted fst-italic">Seleccione un médico y una fecha para ver los horarios disponibles.</div>';
        
        for (let i = 0; i < options.length; i++) {
            const option = options[i];
            if (option.value === "") {
                option.style.display = "block";
                continue;
            }
            const medicoEspecialidad = option.getAttribute('data-especialidad');
            if (selectedEspecialidad === "" || medicoEspecialidad === selectedEspecialidad) {
                option.style.display = "block";
            } else {
                option.style.display = "none";
            }
        }
    }

    function actualizarHorario() {
        const selectedOption = medicoSelect.options[medicoSelect.selectedIndex];
        const turno = selectedOption.getAttribute('data-turno');

        if (turno) {
            let horarioTexto = "";
            if (turno.toLowerCase() === 'mañana') {
                horarioTexto = "07:00 AM - 01:00 PM";
            } else if (turno.toLowerCase() === 'tarde') {
                horarioTexto = "02:00 PM - 08:00 PM";
            } else {
                horarioTexto = "Consultar con administración";
            }

            rangoHorario.textContent = horarioTexto;
            horarioHelp.style.display = 'block';
        } else {
            horarioHelp.style.display = 'none';
        }
    }
});
