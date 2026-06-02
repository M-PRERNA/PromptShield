(function () {
    var COLUMN_STORAGE_KEY = 'promptshield-history-columns';
    var COLUMN_DEFS = [
        { id: 'id', label: 'ID' },
        { id: 'date', label: 'Date' },
        { id: 'ecosystem', label: 'Ecosystem' },
        { id: 'risk', label: 'Risk' },
        { id: 'score', label: 'Score' },
        { id: 'vulnerabilities', label: 'Vulnerabilities' },
        { id: 'preview', label: 'Preview' },
        { id: 'actions', label: 'Actions' }
    ];

    document.addEventListener('DOMContentLoaded', function () {
        var table = document.getElementById('history-table');
        if (!table) {
            return;
        }

        var tbody = table.querySelector('tbody');
        var tableRows = Array.from(tbody.querySelectorAll('tr'));
        var mobileCards = Array.from(document.querySelectorAll('.history-mobile-card'));
        var filterInputs = Array.from(document.querySelectorAll('.column-filter'));
        var resultCount = document.getElementById('history-result-count');
        var noResults = document.getElementById('history-no-results');
        var columnPanel = document.getElementById('column-panel');
        var columnPanelOptions = document.getElementById('column-panel-options');
        var columnToggleBtn = document.getElementById('column-toggle-btn');
        var clearFiltersBtn = document.getElementById('clear-filters-btn');
        var clearFiltersInline = document.getElementById('clear-filters-inline');
        var visibleColumns = loadColumnVisibility();

        buildColumnPanel();
        applyColumnVisibility();
        applyUrlRiskFilter();
        applyFilters();

        filterInputs.forEach(function (input) {
            input.addEventListener('input', applyFilters);
            input.addEventListener('change', applyFilters);
        });

        if (clearFiltersBtn) {
            clearFiltersBtn.addEventListener('click', clearAllFilters);
        }
        if (clearFiltersInline) {
            clearFiltersInline.addEventListener('click', clearAllFilters);
        }

        if (columnToggleBtn && columnPanel) {
            columnToggleBtn.addEventListener('click', function () {
                var expanded = columnToggleBtn.getAttribute('aria-expanded') === 'true';
                columnToggleBtn.setAttribute('aria-expanded', expanded ? 'false' : 'true');
                columnPanel.hidden = expanded;
            });
        }

        document.addEventListener('click', function (event) {
            if (!columnPanel || columnPanel.hidden) {
                return;
            }
            if (columnPanel.contains(event.target) || (columnToggleBtn && columnToggleBtn.contains(event.target))) {
                return;
            }
            columnPanel.hidden = true;
            if (columnToggleBtn) {
                columnToggleBtn.setAttribute('aria-expanded', 'false');
            }
        });

        function buildColumnPanel() {
            if (!columnPanelOptions) {
                return;
            }
            columnPanelOptions.innerHTML = '';
            COLUMN_DEFS.forEach(function (column) {
                var label = document.createElement('label');
                label.className = 'column-option';

                var checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.value = column.id;
                checkbox.checked = visibleColumns.indexOf(column.id) !== -1;
                checkbox.addEventListener('change', function () {
                    if (checkbox.checked) {
                        if (visibleColumns.indexOf(column.id) === -1) {
                            visibleColumns.push(column.id);
                        }
                    } else if (visibleColumns.length > 1) {
                        visibleColumns = visibleColumns.filter(function (id) {
                            return id !== column.id;
                        });
                    } else {
                        checkbox.checked = true;
                        return;
                    }
                    saveColumnVisibility();
                    applyColumnVisibility();
                });

                var text = document.createElement('span');
                text.textContent = column.label;

                label.appendChild(checkbox);
                label.appendChild(text);
                columnPanelOptions.appendChild(label);
            });
        }

        function loadColumnVisibility() {
            try {
                var stored = localStorage.getItem(COLUMN_STORAGE_KEY);
                if (stored) {
                    var parsed = JSON.parse(stored);
                    if (Array.isArray(parsed) && parsed.length > 0) {
                        return parsed;
                    }
                }
            } catch (error) {
                /* ignore */
            }
            return COLUMN_DEFS.map(function (column) {
                return column.id;
            });
        }

        function saveColumnVisibility() {
            try {
                localStorage.setItem(COLUMN_STORAGE_KEY, JSON.stringify(visibleColumns));
            } catch (error) {
                /* ignore */
            }
        }

        function applyColumnVisibility() {
            COLUMN_DEFS.forEach(function (column) {
                var visible = visibleColumns.indexOf(column.id) !== -1;
                table.querySelectorAll('[data-column="' + column.id + '"]').forEach(function (cell) {
                    cell.classList.toggle('column-hidden', !visible);
                });
            });
        }

        function applyUrlRiskFilter() {
            var params = new URLSearchParams(window.location.search);
            var riskParam = params.get('risk');
            if (!riskParam) {
                return;
            }
            var riskFilter = document.querySelector('[data-filter="risk"]');
            if (!riskFilter) {
                return;
            }
            var levels = riskParam.split(',').map(function (value) {
                return value.trim().toUpperCase();
            }).filter(Boolean);
            if (levels.length === 1) {
                riskFilter.value = levels[0];
            } else if (levels.length > 1) {
                riskFilter.dataset.multiRisk = levels.join(',');
                riskFilter.value = '';
            }
        }

        function clearAllFilters() {
            filterInputs.forEach(function (input) {
                input.value = '';
                delete input.dataset.multiRisk;
            });
            if (window.history.replaceState) {
                window.history.replaceState({}, '', window.location.pathname);
            }
            applyFilters();
        }

        function applyFilters() {
            var filters = collectFilters();
            var visibleCount = 0;

            tableRows.forEach(function (row) {
                var matches = rowMatches(row, filters);
                row.hidden = !matches;
                if (matches) {
                    visibleCount += 1;
                }
            });

            mobileCards.forEach(function (card) {
                card.hidden = !rowMatches(card, filters);
            });

            if (resultCount) {
                resultCount.textContent = visibleCount + ' of ' + tableRows.length + ' scans shown';
            }
            if (noResults) {
                noResults.hidden = visibleCount > 0 || tableRows.length === 0;
            }
        }

        function collectFilters() {
            var filters = {};
            filterInputs.forEach(function (input) {
                var key = input.getAttribute('data-filter');
                if (!key) {
                    return;
                }
                if (key === 'risk' && input.dataset.multiRisk) {
                    filters[key] = input.dataset.multiRisk.split(',');
                } else {
                    filters[key] = input.value.trim();
                }
            });
            return filters;
        }

        function rowMatches(row, filters) {
            return Object.keys(filters).every(function (key) {
                return matchesFilter(row, key, filters[key]);
            });
        }

        function matchesFilter(row, key, filterValue) {
            if (!filterValue || (Array.isArray(filterValue) && filterValue.length === 0)) {
                return true;
            }

            var rawValue = row.getAttribute('data-' + key) || '';
            var displayValue = getDisplayValue(row, key);

            if (key === 'risk' && Array.isArray(filterValue)) {
                return filterValue.indexOf(rawValue.toUpperCase()) !== -1;
            }

            if (key === 'risk') {
                return rawValue.toUpperCase() === String(filterValue).toUpperCase();
            }

            if (key === 'ecosystem') {
                return rawValue.toUpperCase() === String(filterValue).toUpperCase();
            }

            if (key === 'id' || key === 'score') {
                return String(rawValue).indexOf(String(filterValue)) !== -1
                    || displayValue.indexOf(String(filterValue).toLowerCase()) !== -1;
            }

            var haystack = (displayValue + ' ' + rawValue).toLowerCase();
            return haystack.indexOf(String(filterValue).toLowerCase()) !== -1;
        }

        function getDisplayValue(row, key) {
            var cell = row.querySelector('[data-column="' + key + '"]');
            if (cell) {
                return cell.textContent.trim().toLowerCase();
            }
            if (key === 'date') {
                return formatDateLabel(row.getAttribute('data-date')).toLowerCase();
            }
            return (row.getAttribute('data-' + key) || '').toLowerCase();
        }

        function formatDateLabel(isoDate) {
            if (!isoDate) {
                return '';
            }
            var date = new Date(isoDate);
            if (Number.isNaN(date.getTime())) {
                return isoDate;
            }
            return date.toLocaleDateString(undefined, {
                day: '2-digit',
                month: 'short',
                year: 'numeric'
            });
        }
    });
})();
