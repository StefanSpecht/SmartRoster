package dom.company.thesis.ga;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

class IslandsView extends JPanel implements IslandEvolutionObserver<Object>
{
    static final long serialVersionUID = -6889043350002030793L;
    
	private static final String FITTEST_INDIVIDUAL_LABEL = "Fittest Individual";
    private static final String MEAN_FITNESS_LABEL = "Mean Fitness/Standard Deviation";

    private final DefaultCategoryDataset bestDataSet = new DefaultCategoryDataset();
    private final DefaultStatisticalCategoryDataset meanDataSet = new DefaultStatisticalCategoryDataset();

    private final JFreeChart chart;

    private final AtomicInteger islandCount = new AtomicInteger(0);
    private double max = 0;
    private final StatisticalLineAndShapeRenderer meanRenderer = new StatisticalLineAndShapeRenderer();


    IslandsView()
    {
        super(new BorderLayout());
        chart = ChartFactory.createBarChart("Island Population Fitness",
                                            "Island No.",
                                            "Candidate Fitness",
                                            bestDataSet,
                                            PlotOrientation.VERTICAL,
                                            true, // Legend
                                            false, // Tooltips
                                            false); // URLs
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.getRangeAxis().setAutoRange(false);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        meanRenderer.setBaseLinesVisible(false);
        ChartPanel chartPanel = new ChartPanel(chart,
                                               ChartPanel.DEFAULT_WIDTH,
                                               ChartPanel.DEFAULT_HEIGHT,
                                               ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH,
                                               ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT,
                                               ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH,
                                               ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT,
                                               false, // Buffered
                                               false, // Properties
                                               true, // Save
                                               true, // Print
                                               false, // Zoom
                                               false); // Tooltips
        add(chartPanel, BorderLayout.CENTER);
        add(createControls(), BorderLayout.SOUTH);
    }

    private JComponent createControls()
    {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JCheckBox meanCheckBox = new JCheckBox("Show Mean and Standard Deviation", false);
        meanCheckBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                chart.setNotify(false);
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    plot.setDataset(1, meanDataSet);
                    plot.setRenderer(1, meanRenderer);
                }
                else
                {
                    plot.setDataset(1, null);
                    plot.setRenderer(1, null);
                }
                chart.setNotify(true);
            }
        });
        controls.add(meanCheckBox);

        return controls;
    }

    public void islandPopulationUpdate(final int islandIndex, final PopulationData<? extends Object> populationData)
    {
        // Make sure the bars are added to the chart in order of island index, regardless of which island
        // reports its results first.
        if (islandIndex >= islandCount.get())
        {
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        // Don't need synchronisation here because SwingUtilities queues these updates
                        // and if a second update gets queued, the loop will be a no-op so it's not a problem.
                        for (Integer i = islandCount.get(); i <= islandIndex; i++)
                        {
                            bestDataSet.addValue(0, FITTEST_INDIVIDUAL_LABEL, i);
                            meanDataSet.add(0, 0, MEAN_FITNESS_LABEL, i);
                            islandCount.incrementAndGet();
                        }
                    }
                });
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            catch (InvocationTargetException ex)
            {
                throw new IllegalStateException(ex.getCause());
            }
        }

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                chart.setNotify(false);
                bestDataSet.setValue(populationData.getBestCandidateFitness(), FITTEST_INDIVIDUAL_LABEL, (Integer) islandIndex);
                meanDataSet.remove(MEAN_FITNESS_LABEL, (Integer) islandIndex);
                meanDataSet.add(populationData.getMeanFitness(),
                                populationData.getFitnessStandardDeviation(),
                                MEAN_FITNESS_LABEL,
                                (Integer) islandIndex);
                ValueAxis rangeAxis = ((CategoryPlot) chart.getPlot()).getRangeAxis();
                // If the range is not sufficient to display all values, enlarge it.
                max = Math.max(max, populationData.getBestCandidateFitness());
                max = Math.max(max, populationData.getMeanFitness() + populationData.getFitnessStandardDeviation());
                while (max > rangeAxis.getUpperBound())
                {
                    rangeAxis.setUpperBound(rangeAxis.getUpperBound() * 2);
                }
                chart.setNotify(true);
            }
        });
    }

    public void populationUpdate(PopulationData<? extends Object> populationData)
    {
        // Do nothing.
    }
}
