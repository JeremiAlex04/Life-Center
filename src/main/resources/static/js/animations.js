// ============================================
// LifeCenter - Animaciones y Efectos Visuales
// Scroll reveal, parallax, contadores animados
// ============================================

document.addEventListener('DOMContentLoaded', function () {

    // === 1. SCROLL REVEAL ANIMATIONS ===
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const scrollObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-fadeInUp');
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);

    // Aplicar a elementos con clase 'reveal'
    document.querySelectorAll('.reveal').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = 'opacity 0.6s ease-out, transform 0.6s ease-out';
        scrollObserver.observe(el);
    });

    // === 2. CONTADORES ANIMADOS ===
    const animateCounter = (element, target, duration = 2000) => {
        const start = 0;
        const increment = target / (duration / 16);
        let current = start;

        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                element.textContent = target;
                clearInterval(timer);
            } else {
                element.textContent = Math.floor(current);
            }
        }, 16);
    };

    const counterObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && !entry.target.classList.contains('counted')) {
                const target = parseInt(entry.target.getAttribute('data-target'));
                animateCounter(entry.target, target);
                entry.target.classList.add('counted');
            }
        });
    }, { threshold: 0.5 });

    document.querySelectorAll('.counter').forEach(counter => {
        counterObserver.observe(counter);
    });

    // === 3. PARALLAX SUAVE ===
    let ticking = false;

    const parallaxElements = document.querySelectorAll('[data-parallax]');

    const updateParallax = () => {
        const scrolled = window.pageYOffset;

        parallaxElements.forEach(element => {
            const speed = element.getAttribute('data-parallax') || 0.5;
            const yPos = -(scrolled * speed);
            element.style.transform = `translateY(${yPos}px)`;
        });

        ticking = false;
    };

    if (parallaxElements.length > 0) {
        window.addEventListener('scroll', () => {
            if (!ticking) {
                window.requestAnimationFrame(updateParallax);
                ticking = true;
            }
        });
    }

    // === 4. NAVBAR SCROLL EFFECT ===
    const navbar = document.querySelector('.navbar');
    let lastScroll = 0;

    window.addEventListener('scroll', () => {
        const currentScroll = window.pageYOffset;

        if (currentScroll > 100) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }

        lastScroll = currentScroll;
    });

    // === 5. SMOOTH SCROLL MEJORADO ===
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            if (href !== '#' && href !== '#!') {
                e.preventDefault();
                const target = document.querySelector(href);
                if (target) {
                    const offsetTop = target.offsetTop - 80;
                    window.scrollTo({
                        top: offsetTop,
                        behavior: 'smooth'
                    });
                }
            }
        });
    });

    // === 6. ANIMACIÓN DE ENTRADA ESCALONADA ===
    const staggerElements = document.querySelectorAll('.stagger-item');

    const staggerObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const items = entry.target.querySelectorAll('.stagger-child');
                items.forEach((item, index) => {
                    setTimeout(() => {
                        item.classList.add('animate-fadeInUp');
                        item.style.opacity = '1';
                        item.style.transform = 'translateY(0)';
                    }, index * 100);
                });
                staggerObserver.unobserve(entry.target);
            }
        });
    }, { threshold: 0.2 });

    staggerElements.forEach(el => {
        const children = el.querySelectorAll('.stagger-child');
        children.forEach(child => {
            child.style.opacity = '0';
            child.style.transform = 'translateY(20px)';
            child.style.transition = 'opacity 0.5s ease-out, transform 0.5s ease-out';
        });
        staggerObserver.observe(el);
    });

    // === 7. PROGRESS BAR DE SCROLL ===
    const progressBar = document.createElement('div');
    progressBar.className = 'scroll-progress';
    progressBar.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 0%;
        height: 3px;
        background: linear-gradient(90deg, #2563EB, #10B981);
        z-index: 9999;
        transition: width 0.1s ease-out;
    `;
    document.body.appendChild(progressBar);

    window.addEventListener('scroll', () => {
        const windowHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
        const scrolled = (window.pageYOffset / windowHeight) * 100;
        progressBar.style.width = scrolled + '%';
    });

    // === 8. HOVER EFFECT EN CARDS ===
    document.querySelectorAll('.card, .card-medico, .service-card, .step-card').forEach(card => {
        card.addEventListener('mouseenter', function () {
            this.style.transform = 'translateY(-10px) scale(1.02)';
        });

        card.addEventListener('mouseleave', function () {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });

    // === 9. ANIMACIÓN DE ICONOS AL HOVER ===
    document.querySelectorAll('.bi, i[class*="bi-"]').forEach(icon => {
        icon.parentElement.addEventListener('mouseenter', function () {
            icon.style.transform = 'scale(1.2) rotate(5deg)';
            icon.style.transition = 'transform 0.3s ease';
        });

        icon.parentElement.addEventListener('mouseleave', function () {
            icon.style.transform = 'scale(1) rotate(0deg)';
        });
    });

    console.log('✨ Animaciones de LifeCenter cargadas correctamente');
});
