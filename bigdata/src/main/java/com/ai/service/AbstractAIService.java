package com.ai.service;

import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAIService {

    protected Evaluator evaluator;

    public Object score(Object inputModel) {
        Map<String, Object> data = extractDataFromInput(inputModel);
        List<InputField> inputFields = evaluator.getInputFields();
        //构造模型输入
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
        for (InputField inputField : inputFields) {
            FieldName inputFieldName = inputField.getName();
            Object rawValue = data.get(inputFieldName.getValue());
            FieldValue inputFieldValue = inputField.prepare(rawValue);
            arguments.put(inputFieldName, inputFieldValue);
        }

        Map<FieldName, ?> results = evaluator.evaluate(arguments);
        List<TargetField> targetFields = evaluator.getTargetFields();

        TargetField targetField = targetFields.get(0);
        FieldName targetFieldName = targetField.getName();

        Object targetFieldValue = results.get(targetFieldName);
        System.out.println("target: " + targetFieldName.getValue() + " value: " + targetFieldValue);
        Object primitiveValue = -1;
        if (targetFieldValue instanceof Computable) {
            Computable computable = (Computable) targetFieldValue;
            if (evaluator.getTargetFields().size() == 1 && "double".equalsIgnoreCase(evaluator.getTargetFields().get(0).getField().getDataType().value())) {
                primitiveValue = (Double) computable.getResult();
            }
            if (evaluator.getTargetFields().size() == 1 && "integer".equalsIgnoreCase(evaluator.getTargetFields().get(0).getField().getDataType().value())) {
                primitiveValue = (Integer) computable.getResult();
            }
        }

        return primitiveValue;
    }

    protected abstract Map<String, Object> extractDataFromInput(Object inputModel);

}
