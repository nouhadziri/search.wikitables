package ca.ualberta.benchmark.evaluation;

import java.util.DoubleSummaryStatistics;

/**
 * Created by nouhadziri on 2017-04-17.
 */
public class IREvaluator {

    private final DoubleSummaryStatistics MAP;
    private final DoubleSummaryStatistics RP;
    private final DoubleSummaryStatistics[] INTERP;

    public IREvaluator() {
        this.INTERP = new DoubleSummaryStatistics[QueryEvaluationResult.POINTS_11];
        for (int i = 0; i < INTERP.length; i++) {
            INTERP[i] = new DoubleSummaryStatistics();
        }

        this.MAP = new DoubleSummaryStatistics();
        this.RP = new DoubleSummaryStatistics();
    }



    public void add(QueryEvaluationResult evaluationResult) {
        MAP.accept(evaluationResult.getAveragePrecision());
        RP.accept(evaluationResult.getRPrecision());

        final double[] interp = evaluationResult.getInterpolatedPrecision();

        for (int i = 0; i < INTERP.length; i++)
            INTERP[i].accept(interp[i]);
    }

    public double getMeanAveragePrecision() {
        return MAP.getAverage();
    }

    public double getRPrecision() {
        return RP.getAverage();
    }

    public double[] getInterpolatedAveragePrecision() {
        double[] interp = new double[INTERP.length];
        for (int i = 0; i < interp.length; i++) {
            interp[i] = INTERP[i].getAverage();
        }

        return interp;
    }

    public String getSummary() {
        String summary = String.format("MAP=%.4f\n", getMeanAveragePrecision());
        summary += String.format("RPrecision=%.4f\n", getRPrecision());
        summary += "Interp=\n";

        for (int i = 0; i < getInterpolatedAveragePrecision().length; i++) {
            summary += String.format("%.1f\t%.4f\n", 0.1 * i, getInterpolatedAveragePrecision()[i]);
        }

        return summary;
    }

}
