# 参数校验
为了控制用户输入内容，在前端校验的基础上，需要在后端接口中对传递的参数数据做进一步校验，保证系统的安全性和数据的完整性。
同时也兼顾对访问日志的记录。约定一下接口参数校验方法。

## RequestBody校验
```java
@PostMapping("/logPostTest")
@ApiOperation("日志测试Post")
@LogOperation("日志测试Post")
public Result<?> logPostTest(@RequestBody @Validated TestForm form) {
    // ValidatorUtils.validateEntity(form, CommonForm.OneGroup.class);
    // todo 业务逻辑
    return new Result<>();
}
```
对于上述代码，可能会出现下列错误
1. 传递的内容不符合json格式，或者传递内容不符合数据类型定义(比如CommonForm中定义的id为Long，但是传了一个非Long型的数据)。会抛出HttpMessageNotReadableException异常。
2. 传递的内容校验规则，对于TestForm中不符合TestForm中约束(比如@NotEmpty)的字段。会抛出MethodArgumentNotValidException异常。

## RequestParam校验
```java
@GetMapping("/logGetTest")
@ApiOperation("日志测试Get")
@LogOperation("日志测试Get")
public Result<?> logGetTest(@ApiParam(value = "xx的id", required = false) @Max(value = 10, message = "不允许超过10") @RequestParam Long id1,
                         @ApiParam(value = "xx的id2", required = true) @NotNull(message = "{pid.require}") @RequestParam(required = false) Long id2) {
    // todo 业务逻辑
    return new Result<>();
}
```
对于上述代码，可能会出现下列错误
1. 对于request=true的参数未传或者传空，会抛出MissingServletRequestParameterException异常。
2. 对于参数传了非指定类型的，比如Long型传了String，会抛出MethodArgumentTypeMismatchException异常。

## 异常处理
对于上述提到的抛出异常的流程，可以在RestControllerAdvice中捕捉处理异常，而程序不会进入方法，也就是无法进入LogOperation切片。

## ValidateGroup
对于同一个RequestBody可以指定不同的校验规则，在constraints后面可以指定校验规则，然后在接受参数中可以@Validated(value = {DefaultGroup.class, AddGroup.class})指定校验规则。
也可以使用ValidatorUtils.validateEntity手动校验，同样可以指定校验规则。有时为了便于定位问题，可以不在参数中使用@Validated校验，而是将校验规则放在逻辑中。这样就可以进入LogOperation切片，记录更多信息。

## ref
1. [SpringBoot参数校验和国际化使用](https://www.jianshu.com/p/46eda1f96abe) 
2. [spring mvc validation](https://www.jianshu.com/p/bcc5a3c86480) 
3. [Spring Boot 参数校验 Validation 入门](https://mp.weixin.qq.com/s/s7ATY6FMcWnAFfFqDEAohg)

