  am5.ready(function() {

    // Create root element
    var root = am5.Root.new("chartdiv");

    // Set themes
    root.setThemes([am5themes_Animated.new(root)]);

    // Create chart
    var chart = root.container.children.push(am5xy.XYChart.new(root, {
        panX: true,
        panY: true,
        wheelX: "panX",
        wheelY: "zoomX"
    }));

    // Create axes
    var dateAxis = chart.xAxes.push(am5xy.DateAxis.new(root, {
        groupData: true,
        baseInterval: {
            timeUnit: "day",
            count: 1
        },
        renderer: am5xy.AxisRendererX.new(root, {}),
        tooltip: am5.Tooltip.new(root, {})
    }));

    var valueAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
        renderer: am5xy.AxisRendererY.new(root, {})
    }));

    // Add series
    var series = chart.series.push(am5xy.LineSeries.new(root, {
        name: "Series",
        xAxis: dateAxis,
        yAxis: valueAxis,
        valueYField: "duration",
        valueXField: "date"
    }));

    // Fetch chart data
    fetch('/chart-data')
        .then(response => response.json())
        .then(data => {
            // Convert map to array of objects
            const dataArray = data.data.map(item => ({
                date: new Date(item.date),
                duration: Number(item.duration)
            }));
            console.log(dataArray); // Output the received data to the console
            // Set data
            series.data.setAll(dataArray);
        });
});
