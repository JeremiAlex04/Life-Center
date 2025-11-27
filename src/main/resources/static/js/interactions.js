// ============================================
// LifeCenter - Microinteracciones
// Ripple effect, feedback visual, hover effects
// ============================================

document.addEventListener('DOMContentLoaded', function () {

    // === 1. RIPPLE EFFECT EN BOTONES ===
    const createRipple = (event) => {
        const button = event.currentTarget;
        const ripple = document.createElement('span');
        const rect = button.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);
        const x = event.clientX - rect.left - size / 2;
        const y = event.clientY - rect.top - size / 2;

        ripple.style.cssText = `
            position: absolute;
            width: ${size}px;
            height: ${size}px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.6);
            left: ${x}px;
            top: ${y}px;
            transform: scale(0);
            animation: ripple 0.6s ease-out;
            pointer-events: none;
        `;

        button.style.position = 'relative';
        button.style.overflow = 'hidden';
        button.appendChild(ripple);

        setTimeout(() => ripple.remove(), 600);
    };

    // Añadir estilo de animación ripple
    const style = document.createElement('style');
    style.textContent = `
        @keyframes ripple {
            to {
                transform: scale(4);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);

    // Aplicar a todos los botones
    document.querySelectorAll('.btn, button').forEach(button => {
        button.addEventListener('click', createRipple);
    });

    // === 2. FEEDBACK VISUAL EN INPUTS ===
    document.querySelectorAll('input, textarea, select').forEach(input => {
        input.addEventListener('focus', function () {
            this.parentElement.classList.add('input-focused');

            // Animación de label flotante si existe
            const label = this.parentElement.querySelector('label');
            if (label) {
                label.style.transform = 'translateY(-25px) scale(0.85)';
                label.style.color = '#2563EB';
            }
        });

        input.addEventListener('blur', function () {
            this.parentElement.classList.remove('input-focused');

            const label = this.parentElement.querySelector('label');
            if (label && !this.value) {
                label.style.transform = 'translateY(0) scale(1)';
                label.style.color = '#6B7280';
            }
        });

        // Animación al escribir
        input.addEventListener('input', function () {
            if (this.value) {
                this.classList.add('has-value');
            } else {
                this.classList.remove('has-value');
            }
        });
    });

    // === 3. BADGE PULSE ANIMATION ===
    document.querySelectorAll('.badge, .specialty-badge').forEach(badge => {
        badge.addEventListener('mouseenter', function () {
            this.style.animation = 'pulse 0.5s ease';
        });

        badge.addEventListener('animationend', function () {
            this.style.animation = '';
        });
    });

    // === 4. TOOLTIP PERSONALIZADO ===
    const createTooltip = (element, text) => {
        const tooltip = document.createElement('div');
        tooltip.className = 'custom-tooltip';
        tooltip.textContent = text;
        tooltip.style.cssText = `
            position: absolute;
            background: #1F2937;
            color: white;
            padding: 8px 12px;
            border-radius: 6px;
            font-size: 14px;
            white-space: nowrap;
            z-index: 10000;
            opacity: 0;
            transform: translateY(10px);
            transition: opacity 0.3s, transform 0.3s;
            pointer-events: none;
        `;
        document.body.appendChild(tooltip);

        const rect = element.getBoundingClientRect();
        tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
        tooltip.style.top = rect.bottom + 10 + 'px';

        setTimeout(() => {
            tooltip.style.opacity = '1';
            tooltip.style.transform = 'translateY(0)';
        }, 10);

        return tooltip;
    };

    document.querySelectorAll('[data-tooltip]').forEach(element => {
        let tooltip = null;

        element.addEventListener('mouseenter', function () {
            const text = this.getAttribute('data-tooltip');
            tooltip = createTooltip(this, text);
        });

        element.addEventListener('mouseleave', function () {
            if (tooltip) {
                tooltip.style.opacity = '0';
                tooltip.style.transform = 'translateY(10px)';
                setTimeout(() => tooltip.remove(), 300);
            }
        });
    });

    // === 5. LOADING STATE EN BOTONES ===
    document.querySelectorAll('[data-loading]').forEach(button => {
        button.addEventListener('click', function () {
            if (!this.classList.contains('loading')) {
                this.classList.add('loading');
                this.disabled = true;

                const originalText = this.innerHTML;
                this.innerHTML = '<i class="bi bi-arrow-repeat spin"></i> Cargando...';

                // Simular carga (en producción esto vendría del backend)
                setTimeout(() => {
                    this.classList.remove('loading');
                    this.disabled = false;
                    this.innerHTML = originalText;
                }, 2000);
            }
        });
    });

    // Estilo para el spinner
    const spinnerStyle = document.createElement('style');
    spinnerStyle.textContent = `
        .spin {
            animation: spin 1s linear infinite;
        }
        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
    `;
    document.head.appendChild(spinnerStyle);

    // === 6. SHAKE EFFECT EN VALIDACIÓN ===
    const shakeElement = (element) => {
        element.style.animation = 'shake 0.5s';
        element.addEventListener('animationend', () => {
            element.style.animation = '';
        }, { once: true });
    };

    const shakeStyle = document.createElement('style');
    shakeStyle.textContent = `
        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
            20%, 40%, 60%, 80% { transform: translateX(5px); }
        }
    `;
    document.head.appendChild(shakeStyle);

    // Aplicar shake a inputs inválidos
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function (e) {
            const invalidInputs = this.querySelectorAll(':invalid');
            if (invalidInputs.length > 0) {
                e.preventDefault();
                invalidInputs.forEach(input => shakeElement(input));
            }
        });
    });

    // === 7. CARD TILT EFFECT ===
    document.querySelectorAll('.card-medico, .service-card').forEach(card => {
        card.addEventListener('mousemove', function (e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const centerX = rect.width / 2;
            const centerY = rect.height / 2;

            const rotateX = (y - centerY) / 20;
            const rotateY = (centerX - x) / 20;

            this.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.02)`;
        });

        card.addEventListener('mouseleave', function () {
            this.style.transform = 'perspective(1000px) rotateX(0) rotateY(0) scale(1)';
        });
    });

    // === 8. COPY TO CLIPBOARD CON FEEDBACK ===
    document.querySelectorAll('[data-copy]').forEach(element => {
        element.addEventListener('click', function () {
            const text = this.getAttribute('data-copy');
            navigator.clipboard.writeText(text).then(() => {
                const feedback = document.createElement('span');
                feedback.textContent = '✓ Copiado';
                feedback.style.cssText = `
                    position: absolute;
                    background: #10B981;
                    color: white;
                    padding: 4px 8px;
                    border-radius: 4px;
                    font-size: 12px;
                    margin-left: 10px;
                    animation: fadeOut 2s forwards;
                `;
                this.appendChild(feedback);
                setTimeout(() => feedback.remove(), 2000);
            });
        });
    });

    const fadeOutStyle = document.createElement('style');
    fadeOutStyle.textContent = `
        @keyframes fadeOut {
            0% { opacity: 1; }
            70% { opacity: 1; }
            100% { opacity: 0; }
        }
    `;
    document.head.appendChild(fadeOutStyle);

    console.log('🎯 Microinteracciones de LifeCenter cargadas correctamente');
});
