package com.chandler.demo.recommend.algorithm;

import java.util.HashSet;

public class FTRL {

    /**
     *
     * 4个超参数
     */

    //超参数
    private double alpha = 0.1;
    //超参数
    //论文建议设为1
    private double belta = 1;
    //超参数
    //L1正则项
    private double L1 =1;
    //超参数
    //L2正则项
    private double L2 = 1;

    //W,Z,N的初始值
    private int init = 10000;

    //需要迭代求解的参数W
    private double[] W;

    //迭代过程中存储的中间值
    private double[] Z;
    private double[] N;

    public FTRL(int init){
        this.init = init;
        this.W = new double[init];
        this.Z= new double[init];
        this.N= new double[init];
    }

    /**
     * 模型训练
     *
     *  set  特征值 X
     *
     *  label  标签 Y
     *
     *  样本 {Y:(X_1,X_2,X_3)} -> {label:set}
     */
    public void fit(HashSet<Integer> set
            ,int label){

        double p = 0.0;

        // 把各个特征值取出计算参数w
        for(Integer i:set){
            /**
             * 第1步 更新 W[i]
             * 每次迭代要存储3个值 N[i] Z[i] W[i]
             *
             *  N[i] = math.sqrt(math.sum(math.pow(g[i],2)))
             *  g[i]是损失函数对第i个特征值的偏导数
             */

            // sign是符号函数 返回的是参数Z的符号
            // 若Z为负数 返回 -1，正数 返回 1
            int sign = Z[i]< 0 ? -1 : 1;
            /**
             *  以下是个公式
             *  若 z 的绝对值 <= L1正则项 ，w = 0;
             *  除此之外 ，w = ...  (具体看公式)
             */
            if(Math.abs(Z[i]) <= L1){
                W[i] = 0.0;
            }else{
                W[i] =(sign * L1 - Z[i]) /
                        ( (belta - Math.sqrt(N[i])) / alpha + L2 );
            }

            /**
             * 第2步 使用更新后的 W 计算 f(x)
             * f(x) = w * x
             *
             */
            p = getP(W[i],set);
        }



        double mult = label * (1 / (1+Math.exp(-p * label))-1);


        /**
         * 第3步
         * 迭代更新各个参数d
         * 以下是4个公式
         */

        //同样也是把特征值取出计算
        for(Integer i:set){
            // 更新 g 第1个更新公式
            double g = mult * i;
            //第2个更新公式
            double sigma = (Math.sqrt(N[i] + g * g) - Math.sqrt(N[i])) / alpha;
            //更新 Z[i] 第3个更新公式
            Z[i] += g - sigma * W[i];
            //更新 N[i] 第4个更新公式
            N[i] += g * g;
        }

        set.clear();
    }

    private double getP(double w,HashSet<Integer> set){
        double rs = 0.0;
        rs += w;
        for(Integer i:set){
            rs += w * i;
        }
        return rs;
    }

    /**
     *
     *  预测
     */
    public double predict(HashSet<Integer> set) {
        Double p = 0.0;
        for (Integer i : set) {
            p += W[i];
        }
        p = 1 / (1 + Math.exp(-p));

        return p;
    }

}
