package com.ai.utils;

import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;

import java.io.IOException;
import java.io.InputStream;

public class ModelUtils {

    public static Evaluator loadPmml(String modelPath) {
        PMML pmml = null;
        InputStream inputStream = null;
        InputStream is = null;
        try {
            inputStream = ModelUtils.class.getResourceAsStream(modelPath);
            if (inputStream == null) {
                return null;
            }
            is = inputStream;
            pmml = org.jpmml.model.PMMLUtil.unmarshal(is);
            ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
            Evaluator evaluator = modelEvaluatorFactory.newModelEvaluator(pmml);
            return evaluator;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭输入流
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
