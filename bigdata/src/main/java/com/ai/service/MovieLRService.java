package com.ai.service;

import com.ai.models.MovieLRInputModel;
import com.ai.utils.ModelUtils;

import java.util.HashMap;
import java.util.Map;

public class MovieLRService extends AbstractAIService {

    public MovieLRService() {
        this.evaluator = ModelUtils.loadPmml("/models/lr.pmml");
    }

    /**
     * <MiningField name="age"/>
     * <MiningField name="sex"/>
     * <MiningField name="matchFlag"/>
     *
     * @param inputModel
     * @return
     */
    @Override
    protected Map<String, Object> extractDataFromInput(Object inputModel) {
        MovieLRInputModel movieLRInputModel = (MovieLRInputModel) inputModel;
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("age", movieLRInputModel.getAge());
        data.put("sex", movieLRInputModel.getSex());
        data.put("matchFlag", movieLRInputModel.getMatchFlag());
        return data;
    }

}
