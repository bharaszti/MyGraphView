package org.doit.meter.ui;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.jjoe64.graphview_demos.R;

import java.util.Random;

public class DiagramActivity extends ActionBarActivity {
    private static final int MAX_DATA_POINTS = 12;
    private static final int DEFAULT_COLOR = Color.GREEN;
    private static final int SELECTED_COLOR = Color.YELLOW;

    private DataPoint data1[] = new DataPoint[MAX_DATA_POINTS];
    private DataPoint data2[] = new DataPoint[MAX_DATA_POINTS];
    private String labels[] = new String[MAX_DATA_POINTS];
    private Double selectedX;
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);
        setupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_diagram, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupView() {
        // data
        Random random = new Random();
        for (int i = 0; i < MAX_DATA_POINTS; i++) {
            int value = 100 + random.nextInt(20);
            data1[i] = new DataPoint(i, value);
            data2[i] = new DataPoint(i, value + 10 - random.nextInt(20));
            labels[i] = String.valueOf(i + 1);
        }

        this.graph = (GraphView) this.findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(data1);
        graph.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(data2);
        graph.addSeries(series2);

        // style
        series.setSpacing(10);

        // Y axis
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(150);

        // use static labels for X axis
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(labels);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        // selected bar styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                Double selected = DiagramActivity.this.selectedX;
                if (selected != null && selected.equals(data.getX())) {
                    return SELECTED_COLOR;
                }
                return DEFAULT_COLOR;
            }
        });

        // register tap on series callback
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                DiagramActivity.this.selectDataPoint(dataPoint);
            }
        });

        // select default bar
        selectDataPoint(data1[MAX_DATA_POINTS - 1]);
    }

    private void selectDataPoint(DataPointInterface dataPoint) {
        Double x = dataPoint.getX();
        Double y = dataPoint.getY();

        TextView text = (TextView) DiagramActivity.this.findViewById(R.id.section_label);
        text.setText(String.format("Selected: x=%s, y=%d", labels[x.intValue()], y.intValue()));

        this.selectedX = x;
        if (graph != null) {
            graph.invalidate();
        }
    }

}
