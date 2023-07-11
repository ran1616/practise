package com.atguigu.gmall.common.result;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * 全局统一返回结果类
 *
 */
@Data
@ApiModel(value = "全局统一返回结果")
public class Result<T> {                    // code(业务状态码： 200， 401) 、 message(错误提示信息) ， data(业务数据)

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private T data;

    public Result(){}

    // 返回数据
    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        if (data != null)
            result.setData(data);
        return result;
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static<T> Result<T> ok(){
        return Result.ok(null);
    }

    /**
     * 操作成功
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Result<T> ok(T data){
        Result<T> result = build(data);
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> fail(){
        return Result.fail(null);
    }

    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Result<T> fail(T data){
        Result<T> result = build(data);
        return build(data, ResultCodeEnum.FAIL);
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }

    public boolean isOk() {
        if(this.getCode().intValue() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {

        Result result = new Result() ;
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(null);
        System.out.println(JSON.toJSONString(result));

        System.out.println("-------------------------------------------------------");

        Result<Object> ok = Result.ok();
        System.out.println(JSON.toJSONString(ok));

        HashMap<String , String> data = new HashMap<>() ;
        data.put("userName" , "张三") ;
        data.put("password" , "1234") ;
        Result<HashMap<String, String>> hashMapResult = Result.ok(data);
        System.out.println(JSON.toJSONString(hashMapResult));

        Result<Object> fail = Result.fail();
        System.out.println(JSON.toJSONString(fail));

        fail = Result.fail(data);
        System.out.println(JSON.toJSONString(fail));

        System.out.println("-------------------------------------------------------");

        Result<Object> result1 = Result.build(data, ResultCodeEnum.SUCCESS);
        System.out.println(JSON.toJSONString(result1));
    }

}
