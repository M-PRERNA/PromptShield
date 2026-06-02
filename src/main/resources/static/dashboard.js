(function () {
    var trendChartInstance = null;

    function readThemeColors() {
        var styles = getComputedStyle(document.documentElement);
        return {
            grid: styles.getPropertyValue('--md-outline-variant').trim(),
            text: styles.getPropertyValue('--md-on-surface-muted').trim(),
            surface: styles.getPropertyValue('--md-surface').trim(),
            green: styles.getPropertyValue('--md-score-green').trim(),
            orange: styles.getPropertyValue('--md-score-orange').trim(),
            red: styles.getPropertyValue('--md-score-red').trim()
        };
    }

    function scoreStrokeColor(score, colors) {
        colors = colors || readThemeColors();
        if (score >= 80) {
            return colors.green;
        }
        if (score >= 60) {
            return colors.orange;
        }
        return colors.red;
    }

    function withAlpha(color, alphaHex) {
        if (color.indexOf('#') === 0 && color.length === 7) {
            return color + alphaHex;
        }
        return color;
    }

    function riskLabel(risk) {
        if (!risk) {
            return 'Unknown';
        }
        return risk.charAt(0) + risk.slice(1).toLowerCase();
    }

    function initScoreRings() {
        document.querySelectorAll('.score-ring-wrap[data-security-score]').forEach(function (wrap) {
            var score = Number(wrap.getAttribute('data-security-score'));
            if (Number.isNaN(score)) {
                return;
            }
            var fill = wrap.querySelector('.score-ring-fill');
            if (!fill) {
                return;
            }
            var offset = Math.max(0, 100 - score);
            fill.style.strokeDashoffset = String(offset);
            fill.style.stroke = scoreStrokeColor(score);
        });
    }

    function destroyTrendChart() {
        if (trendChartInstance) {
            trendChartInstance.destroy();
            trendChartInstance = null;
        }
    }

    function createThresholdBandsPlugin(colors) {
        return {
            id: 'thresholdBands',
            beforeDatasetsDraw: function (chart) {
                var chartArea = chart.chartArea;
                var yScale = chart.scales.y;
                if (!chartArea || !yScale) {
                    return;
                }
                var ctx = chart.ctx;
                var bands = [
                    { from: 80, to: 100, color: withAlpha(colors.green, '18') },
                    { from: 60, to: 80, color: withAlpha(colors.orange, '18') },
                    { from: 0, to: 60, color: withAlpha(colors.red, '18') }
                ];

                bands.forEach(function (band) {
                    var yTop = yScale.getPixelForValue(band.to);
                    var yBottom = yScale.getPixelForValue(band.from);
                    ctx.save();
                    ctx.fillStyle = band.color;
                    ctx.fillRect(chartArea.left, yTop, chartArea.right - chartArea.left, yBottom - yTop);
                    ctx.restore();
                });

                [80, 60].forEach(function (value, index) {
                    var y = yScale.getPixelForValue(value);
                    ctx.save();
                    ctx.strokeStyle = withAlpha(index === 0 ? colors.green : colors.orange, '66');
                    ctx.lineWidth = 1;
                    ctx.setLineDash([4, 4]);
                    ctx.beginPath();
                    ctx.moveTo(chartArea.left, y);
                    ctx.lineTo(chartArea.right, y);
                    ctx.stroke();
                    ctx.fillStyle = colors.text;
                    ctx.font = '600 11px Inter, sans-serif';
                    ctx.textAlign = 'right';
                    ctx.textBaseline = 'bottom';
                    ctx.fillText(index === 0 ? 'Ready' : 'Review', chartArea.right - 4, y - 4);
                    ctx.restore();
                });
            }
        };
    }

    function initTrendChart() {
        var canvas = document.getElementById('scoreTrendChart');
        if (!canvas || typeof Chart === 'undefined') {
            return;
        }

        var labelsRaw = canvas.getAttribute('data-labels') || '';
        var scoresRaw = canvas.getAttribute('data-scores') || '';
        if (!labelsRaw || !scoresRaw) {
            return;
        }

        destroyTrendChart();

        var labels = labelsRaw.split('|');
        var scores = scoresRaw.split('|').map(Number);
        var scanIds = (canvas.getAttribute('data-scan-ids') || '').split('|');
        var risks = (canvas.getAttribute('data-risks') || '').split('|');
        var colors = readThemeColors();
        var pointColors = scores.map(function (score) {
            return scoreStrokeColor(score, colors);
        });
        var latestScore = scores.length > 0 ? scores[scores.length - 1] : 0;
        var pointPad = 10;

        trendChartInstance = new Chart(canvas, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Security Score',
                    data: scores,
                    clip: false,
                    borderColor: scoreStrokeColor(latestScore, colors),
                    segment: {
                        borderColor: function (context) {
                            var value = context.p1.parsed.y;
                            return scoreStrokeColor(value, colors);
                        }
                    },
                    backgroundColor: function (context) {
                        var chart = context.chart;
                        var chartArea = chart.chartArea;
                        if (!chartArea) {
                            return withAlpha(scoreStrokeColor(latestScore, colors), '33');
                        }
                        var gradient = chart.ctx.createLinearGradient(0, chartArea.top, 0, chartArea.bottom);
                        var tint = scoreStrokeColor(latestScore, colors);
                        gradient.addColorStop(0, withAlpha(tint, '44'));
                        gradient.addColorStop(1, withAlpha(tint, '00'));
                        return gradient;
                    },
                    fill: true,
                    tension: 0.4,
                    pointRadius: 5,
                    pointHoverRadius: 8,
                    pointBackgroundColor: pointColors,
                    pointBorderColor: colors.surface,
                    pointBorderWidth: 2,
                    pointHoverBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                clip: false,
                layout: {
                    padding: {
                        top: 14,
                        bottom: 8,
                        left: 8,
                        right: 14
                    }
                },
                animation: {
                    duration: 600,
                    easing: 'easeOutQuart'
                },
                interaction: {
                    mode: 'nearest',
                    intersect: false
                },
                onHover: function (event, elements) {
                    canvas.style.cursor = elements.length > 0 ? 'pointer' : 'default';
                },
                onClick: function (event, elements) {
                    if (elements.length === 0) {
                        return;
                    }
                    var index = elements[0].index;
                    var scanId = scanIds[index];
                    if (scanId) {
                        window.location.href = '/history/' + scanId;
                    }
                },
                scales: {
                    y: {
                        min: 0 - pointPad,
                        max: 100 + pointPad,
                        grid: { color: colors.grid },
                        ticks: {
                            color: colors.text,
                            stepSize: 20,
                            callback: function (value) {
                                if (value < 0 || value > 100) {
                                    return '';
                                }
                                return value + '%';
                            }
                        },
                        title: {
                            display: true,
                            text: 'Security score',
                            color: colors.text,
                            font: { size: 13, weight: '600' }
                        }
                    },
                    x: {
                        offset: true,
                        grid: { display: false },
                        ticks: {
                            color: colors.text,
                            minRotation: 90,
                            maxRotation: 90,
                            autoSkip: true,
                            maxTicksLimit: 12
                        },
                        title: {
                            display: true,
                            text: 'Scan date',
                            color: colors.text,
                            font: { size: 13, weight: '600' }
                        }
                    }
                },
                plugins: {
                    legend: { display: false },
                    filler: {
                        clip: false
                    },
                    tooltip: {
                        backgroundColor: colors.surface,
                        titleColor: colors.text,
                        bodyColor: colors.text,
                        borderColor: colors.grid,
                        borderWidth: 1,
                        padding: 12,
                        displayColors: false,
                        callbacks: {
                            title: function (items) {
                                return items.length > 0 ? items[0].label : '';
                            },
                            label: function (context) {
                                var score = context.parsed.y;
                                var risk = risks[context.dataIndex] || '';
                                return [
                                    'Score: ' + score + '%',
                                    'Risk: ' + riskLabel(risk)
                                ];
                            },
                            afterLabel: function () {
                                return 'Click to view scan';
                            }
                        }
                    }
                }
            },
            plugins: [createThresholdBandsPlugin(colors)]
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        initScoreRings();
        initTrendChart();
    });

    document.addEventListener('themechange', function () {
        initScoreRings();
        initTrendChart();
    });
})();
