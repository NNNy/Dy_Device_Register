package com.example.xg;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookXg implements IXposedHookLoadPackage {

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        final String packageName = loadPackageParam.packageName;
        if(!packageName.equals("com.ss.android.ugc.aweme")){
            return;
        }

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                final ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                final Class<?> xgClazz = cl.loadClass("com.ss.sys.ces.gg.tt$1");

                // TODO HOOK XG生成
                XposedHelpers.findAndHookMethod(xgClazz, "a", String.class, Map.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Log.e("***tag***", "sig-args[0]"+ param.args[0].toString());
                        Log.e("***tag***", "sig-args[1]"+ param.args[1].toString());
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.e("***tag***", "map:"+ JSON.toJSONString(param.getResult()));
                    }
                });


                // TODO Hook so加密开关
                final Class<?> devicedClazz = cl.loadClass("com.bytedance.frameworks.core.encrypt.TTEncryptUtils");
                XposedHelpers.findAndHookMethod(devicedClazz, "a", byte[].class, int.class, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // 重点是设置这个为空(是否so加密的开关)
                        param.setResult(null);

                        byte[] b1 = (byte[]) param.args[0];
                        Log.e("***tag***", "sig-args[0]"+ new String(b1));
                        Log.e("***tag***", "sig-args[1]"+ param.args[1]);
                        if ((int)param.args[1] > 180){
                            String encoded = Bs64.encode(b1);
                            Log.e("***gzip base64***:", encoded);
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        byte[] b2 = (byte[]) param.getResult();
                        if (b2.length > 180){
                            // so加密之后的密文
                            String encoded = Bs64.encode(b2);
                            Log.e("***base64-ttEncrypt***:", encoded);
                        }
                    }
                });
            }
        });
    }
}