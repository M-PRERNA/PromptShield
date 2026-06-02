(function () {
    document.addEventListener('DOMContentLoaded', function () {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebar-overlay');
        const menuToggle = document.getElementById('menu-toggle');

        if (!sidebar || !menuToggle) {
            return;
        }

        function setOpen(open) {
            sidebar.classList.toggle('open', open);
            if (overlay) {
                overlay.classList.toggle('visible', open);
                overlay.setAttribute('aria-hidden', open ? 'false' : 'true');
            }
            menuToggle.setAttribute('aria-expanded', open ? 'true' : 'false');
            menuToggle.setAttribute('aria-label', open ? 'Close navigation menu' : 'Open navigation menu');
        }

        menuToggle.addEventListener('click', function () {
            setOpen(!sidebar.classList.contains('open'));
        });

        if (overlay) {
            overlay.addEventListener('click', function () {
                setOpen(false);
            });
        }

        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                setOpen(false);
            }
        });
    });
})();
