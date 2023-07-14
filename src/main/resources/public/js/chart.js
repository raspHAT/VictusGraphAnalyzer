// Create root element
// https://www.amcharts.com/docs/v5/getting-started/#Root_element
var root = am5.Root.new("chartdiv");


// Set themes
// https://www.amcharts.com/docs/v5/concepts/themes/
root.setThemes([
    am5themes_Animated.new(root)
]);

// Specify date fields, so that they are formatted accordingly in tooltips
// https://www.amcharts.com/docs/v5/concepts/formatters/data-placeholders/#Formatting_placeholders
root.dateFormatter.setAll({
    dateFormat: "yyyy-MM-dd",
    dateFields: ["valueX"]
});


// Create chart
// https://www.amcharts.com/docs/v5/charts/xy-chart/
var chart = root.container.children.push(am5xy.XYChart.new(root, {
    panX: false,
    panY: false,
    wheelX: "panX",
    wheelY: "zoomX"
}));


// Add cursor
// https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
    behavior: "zoomX"
}));
cursor.lineY.set("visible", false);

// Declare chartData variable
var chartData;

// Fetch chart data
fetch('/chart-data')
    .then(response => {
    if (!response.ok) {
        throw new Error('Network response was not ok');
    }
    return response.json();
    })
    .then(data => {
    // Store the fetched data in the chartData variable
    chartData = data;

    // Use the chartData variable as needed
    series.data.setAll(chartData.data);

    // Make stuff animate on load
    series.appear(1000);
    chart.appear(1000, 100);
    })
    .catch(error => {
    console.error('Error fetching chart data:', error);
    // Handle the error or display an error message to the user
});



// Create axes
// https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
var xAxis = chart.xAxes.push(am5xy.DateAxis.new(root, {
    //maxDeviation: 0.5,
    baseInterval: {
    timeUnit: "day",
    count: 1
    },
    renderer: am5xy.AxisRendererX.new(root, {
    pan:"zoom"
    }),
    tooltip: am5.Tooltip.new(root, {})
}));

var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
    //maxDeviation:1,
    renderer: am5xy.AxisRendererY.new(root, {
    pan:"zoom"
    })
}));


// Add series
// https://www.amcharts.com/docs/v5/charts/xy-chart/series/
var series = chart.series.push(am5xy.LineSeries.new(root, {
    name: "Series",
    xAxis: xAxis,
    yAxis: yAxis,
    valueYField: "value",
    valueXField: "date",
    tooltip: am5.Tooltip.new(root, {
    labelText: "{valueX}: {valueY}"
    })
}));

// Add scrollbar
// https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
chart.set("scrollbarX", am5.Scrollbar.new(root, {
    orientation: "horizontal"
}));

chart.set("scrollbarY", am5.Scrollbar.new(root, {
    orientation: "vertical"
}));


// Set data
// var data = generateDatas(1200);
//series.data.setAll(data);
//series.data.setAll(chartData.data);

// Set data
//if (chartData) {
 //   series.data.setAll(chartData.data);
//}


// Make stuff animate on load
// https://www.amcharts.com/docs/v5/concepts/animations/#Forcing_appearance_animation
//series.appear(1000);
//chart.appear(1000, 100);