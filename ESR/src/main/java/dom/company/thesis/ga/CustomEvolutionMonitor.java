package dom.company.thesis.ga;

import java.awt.BorderLayout;
import java.awt.Window;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.uncommons.watchmaker.framework.EvolutionUtils;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.swing.ObjectSwingRenderer;
import org.uncommons.watchmaker.swing.evolutionmonitor.StatusBar;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import dom.company.thesis.gui.RosterRenderer;
import dom.company.thesis.service.InputService;

public class CustomEvolutionMonitor<T> implements IslandEvolutionObserver<T>
{
    private final List<IslandEvolutionObserver<? super T>> views = new LinkedList<IslandEvolutionObserver<? super T>>();

    private JComponent monitorComponent;
    private Window window = null;
    private TerminationCondition[] terminationConditions;

    private final boolean islands;
    private boolean rollup;

    public CustomEvolutionMonitor()
    {
        this(false);
    }

    public CustomEvolutionMonitor(boolean islands)
    {
        this(new ObjectSwingRenderer(), islands, (TerminationCondition[])null);
    }

    public CustomEvolutionMonitor(final Renderer<? super T, JComponent> renderer, boolean islands, TerminationCondition... conditions)
    {
        this.islands = islands;
        this.terminationConditions = conditions;
        if (SwingUtilities.isEventDispatchThread())
        {
            init(renderer);
        }
        else
        {
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        init(renderer);
                    }
                });
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(ex);
            }
            catch (InvocationTargetException ex)
            {
                throw new IllegalStateException(ex);
            }
        }
    }

    
    private void init(Renderer<? super T, JComponent> renderer)
    {
        // Make sure all JFreeChart charts are created with the legacy theme
        // (grey surround and white data area).
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());

        JTabbedPane tabs = new JTabbedPane();
        monitorComponent = new JPanel(new BorderLayout());
        monitorComponent.add(tabs, BorderLayout.CENTER);

        FittestCandidateView<T> candidateView = new FittestCandidateView<T>(renderer);
        tabs.add("Fittest Individual", candidateView);
        views.add(candidateView);

        PopulationFitnessView fitnessView = new PopulationFitnessView(islands);
        tabs.add(islands ? "Global Population" : "Population Fitness", fitnessView);
        views.add(fitnessView);

        if (islands)
        {
            IslandsView islandsView = new IslandsView();
            tabs.add("Island Populations", islandsView);
            views.add(islandsView);
        }

        JVMView jvmView = new JVMView();
        tabs.add("JVM Memory", jvmView);

        StatusBar statusBar = new StatusBar(islands);
        monitorComponent.add(statusBar, BorderLayout.SOUTH);
        views.add(statusBar);
    }

    public void populationUpdate(PopulationData<? extends T> populationData)
    {
    	//if termination condition is met, enable the renderer and log statistics to csv
    	if (EvolutionUtils.shouldContinue(populationData, terminationConditions) != null) {
    		
    		//Enable renderer
    		RosterRenderer.enable();
    		
    		//log statistics to csv
    		final String logPath = InputService.getLogFileFolder();
    		final String rollupLogPath = InputService.getRollupLogFilePath();
    		final String detailedLogPath = InputService.getDetailedLogFilePath();
    		
    		
    		String[] logData = new String[]{
    				String.valueOf(populationData.getPopulationSize()),
    				String.valueOf(populationData.getEliteCount()),
    				String.valueOf(populationData.getBestCandidateFitness()),
    				String.valueOf(populationData.getMeanFitness()),
    				String.valueOf(populationData.getFitnessStandardDeviation()),
    				String.valueOf(populationData.getGenerationNumber()),
    				String.valueOf(populationData.getElapsedTime())        				
    		};
    		
    		try {
    			//read current data
				CSVReader csvReader = new CSVReader(new FileReader(logPath));
				List<String[]> allLogData = csvReader.readAll();
				csvReader.close();
				
				//add new data and write to disk
				allLogData.add(logData);
				CSVWriter csvWriter = new CSVWriter(new FileWriter(logPath));
				csvWriter.writeAll(allLogData);
				csvWriter.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		//rollup data if required
    		if (rollup) {
    			try {
    				int avgPopulationSize;
    				int avgEliteCount;
    				double avgBestCandidateFitness;
    				double avgMeanFitness;
    				double avgStandardDeviation;
    				int avgGenerationNumber;
    				long avgElapsedTime;
    				String[] rollUpData = new String[7];
    				
        			//Read current data, save and remove log header
    				CSVReader csvReader = new CSVReader(new FileReader(logPath));
    				List<String[]> allLogData = csvReader.readAll();
    				String[] logHeader = allLogData.get(0);
    				allLogData.remove(0);
    				csvReader.close();
    				
    				//append current data to detailed log
    				csvReader = new CSVReader(new FileReader(detailedLogPath));
    				List<String[]> allDetailedLogData = csvReader.readAll();
    				csvReader.close();
    				allDetailedLogData.addAll(allLogData);
    				CSVWriter csvWriter = new CSVWriter(new FileWriter(detailedLogPath));
    				csvWriter.writeAll(allDetailedLogData);
    				csvWriter.close();
    				
    				//calculate avg
    				avgPopulationSize = getIntAverage(allLogData,0);
    				avgEliteCount = getIntAverage(allLogData,1);
    				avgBestCandidateFitness = getDoubleAverage(allLogData,2);
    				avgMeanFitness = getDoubleAverage(allLogData,3);
    				avgStandardDeviation = getDoubleAverage(allLogData,4);
    				avgGenerationNumber = getIntAverage(allLogData,5);
    				avgElapsedTime = getLongAverage(allLogData,6);
    				
    				//create new dataset
    				rollUpData[0] = String.valueOf(avgPopulationSize);
    				rollUpData[1] = String.valueOf(avgEliteCount);
    				rollUpData[2] = String.valueOf(avgBestCandidateFitness);
    				rollUpData[3] = String.valueOf(avgMeanFitness);
    				rollUpData[4] = String.valueOf(avgStandardDeviation);
    				rollUpData[5] = String.valueOf(avgGenerationNumber);
    				rollUpData[6] = String.valueOf(avgElapsedTime);
    				
    				//append rollup dataset to rollup file
    				csvReader = new CSVReader(new FileReader(rollupLogPath));
    				List<String[]> allRollupLogData = csvReader.readAll();
    				csvReader.close();
    				allRollupLogData.add(rollUpData);
    				csvWriter = new CSVWriter(new FileWriter(rollupLogPath));
    				csvWriter.writeAll(allRollupLogData);
    				csvWriter.close();
    				
    				//Empty current logfile, except header
    				csvWriter = new CSVWriter(new FileWriter(logPath));
    				csvWriter.writeNext(logHeader);
    				csvWriter.close();
    				
    				//disable rollup for next run
    				setRollup(false);
    				
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    		
    	}
    	
        for (IslandEvolutionObserver<? super T> view : views)
        {
        	view.populationUpdate(populationData);
        }
    }

    public void islandPopulationUpdate(int islandIndex, PopulationData<? extends T> populationData)
    {
        for (IslandEvolutionObserver<? super T> view : views)
        {
            view.islandPopulationUpdate(islandIndex, populationData);
        }
    }


    public JComponent getGUIComponent()
    {
        return monitorComponent;
    }

    public void showInFrame(final String title,
                            final boolean exitOnClose)
    {        
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame frame = new JFrame(title);
                frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
                showWindow(frame);
            }
        });
    }

    public void showInDialog(final JFrame owner,
                             final String title,
                             final boolean modal)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JDialog dialog = new JDialog(owner, title, modal);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                showWindow(dialog);
            }
        });
    }

    private void showWindow(Window newWindow)
    {
        if (window != null)
        {
            window.remove(getGUIComponent());
            window.setVisible(false);
            window.dispose();
            window = null;
        }
        newWindow.add(getGUIComponent(), BorderLayout.CENTER);
        newWindow.pack();
        newWindow.setVisible(true);
        this.window = newWindow;
    }

	public boolean isRollup() {
		return rollup;
	}

	public void setRollup(boolean rollup) {
		this.rollup = rollup;
	} 
	
	private int getIntAverage(List<String[]> statistics, int i) {
		int sum = 0;
		for (String[] dataset : statistics) {
			sum += Integer.valueOf(dataset[i]);
		}
		return sum / statistics.size();
	}
	
	private double getDoubleAverage(List<String[]> statistics, int i) {
		double sum = 0;
		for (String[] dataset : statistics) {
			sum += Double.valueOf(dataset[i]);
		}
		return sum / statistics.size();
	}
	
	private long getLongAverage(List<String[]> statistics, int i) {
		long sum = 0;
		for (String[] dataset : statistics) {
			sum += Long.valueOf(dataset[i]);
		}
		return sum / statistics.size();
	}
}
