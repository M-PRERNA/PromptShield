(function () {
    document.addEventListener('DOMContentLoaded', function () {
        const table = document.getElementById('history-table');
        if (!table) {
            return;
        }

        const tbody = table.querySelector('tbody');
        const headers = table.querySelectorAll('th button[data-sort]');

        headers.forEach(function (button) {
            button.addEventListener('click', function () {
                const type = button.getAttribute('data-sort');
                const column = button.closest('th').getAttribute('data-column');
                const rows = Array.from(tbody.querySelectorAll('tr:not([hidden])'));
                const ascending = button.getAttribute('data-order') !== 'asc';
                button.setAttribute('data-order', ascending ? 'asc' : 'desc');

                rows.sort(function (a, b) {
                    return compareRows(a, b, column, type, ascending);
                });

                rows.forEach(function (row) {
                    tbody.appendChild(row);
                });
            });
        });
    });

    function compareRows(a, b, column, type, ascending) {
        const modifier = ascending ? 1 : -1;
        const cellA = a.querySelector('[data-column="' + column + '"]');
        const cellB = b.querySelector('[data-column="' + column + '"]');

        if (type === 'number') {
            const valA = Number(a.getAttribute('data-' + column)) || (cellA ? cellA.textContent.trim() : '');
            const valB = Number(b.getAttribute('data-' + column)) || (cellB ? cellB.textContent.trim() : '');
            const numA = Number(String(valA).replace('%', ''));
            const numB = Number(String(valB).replace('%', ''));
            if (!Number.isNaN(numA) && !Number.isNaN(numB)) {
                return (numA - numB) * modifier;
            }
        }

        if (type === 'date') {
            const dateA = new Date(a.getAttribute('data-date')).getTime();
            const dateB = new Date(b.getAttribute('data-date')).getTime();
            return (dateA - dateB) * modifier;
        }

        const textA = (a.getAttribute('data-' + column) || (cellA ? cellA.textContent : '')).trim().toLowerCase();
        const textB = (b.getAttribute('data-' + column) || (cellB ? cellB.textContent : '')).trim().toLowerCase();
        return textA.localeCompare(textB) * modifier;
    }
})();
