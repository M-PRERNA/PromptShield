(function () {
    const STORAGE_KEY = 'promptshield-theme';
    const root = document.documentElement;

    function getPreferred() {
        const stored = localStorage.getItem(STORAGE_KEY);
        if (stored === 'light' || stored === 'dark') {
            return stored;
        }
        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    function applyTheme(theme) {
        root.setAttribute('data-theme', theme);
        const meta = document.querySelector('meta[name="color-scheme"]');
        if (meta) {
            meta.setAttribute('content', theme === 'dark' ? 'dark' : 'light');
        }
        const toggle = document.getElementById('theme-toggle');
        if (toggle) {
            toggle.setAttribute('aria-pressed', theme === 'dark' ? 'true' : 'false');
            toggle.setAttribute('aria-label', theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode');
        }
        document.dispatchEvent(new CustomEvent('themechange', { detail: { theme: theme } }));
    }

    applyTheme(getPreferred());

    document.addEventListener('DOMContentLoaded', function () {
        const toggle = document.getElementById('theme-toggle');
        if (!toggle) {
            return;
        }
        toggle.addEventListener('click', function () {
            const next = root.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
            localStorage.setItem(STORAGE_KEY, next);
            applyTheme(next);
        });
    });
})();
