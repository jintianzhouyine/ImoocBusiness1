package com.youdu.module.recommand;

import com.youdu.module.BaseModel;

/**
 * Created by mycomputer on 2017/4/3.
 */

public class BaseRecommandModel extends BaseModel {

    /**
     * 变量名称一定要与json数据中的key值一样
     */
    public String ecode;
    public String emsg;
    public RecommandModel data;
}
