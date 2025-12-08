package com.my;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.RsaPublicJwk;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.ui.Model;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class JwtApp {

    public static void main(String[] args) throws Exception {
        String publickey ="{\"kty\":\"RSA\",\"kid\":\"944408177360855163\",\"alg\":\"RS256\",\"n\":\"vF07fVJRHRnd6N8iQ5HEG4P5Om5013tDLmlvl1CRRIjohwPmaly3kwtm_8AR3cJwJzaJSfRjVmLwzyfg_6sEGR4t_76K8de5e5oJBJJ3CdazGsY6c2mlNzuuy41IipESMB1CpvMIUpOcueuyOcFqwwOy-2AJC3jX2D_wINqhf_iKAcbqD1AvLE6B2LUm8njaJoGiMQ70lKQHA2ssMcAoVLEU-_33BhfRL_g-lDS1nemm_sGlQgubAKuX0zXWbDsHuCYIJzLglxJgMYQZx3vyg9bY2WGexixqJ7tYTdDwcrqG7TRxdvkQkHP0h9mUu2qaBJ3ctd3j9xQU3v6iFw5lEQ\",\"e\":\"AQAB\"}";
String tokne="eyJhbGciOiJSUzI1NiIsImtpZCI6Ijk0NDQwODE3NzM2MDg1NTE2MyJ9.eyJlbWFpbCI6bnVsbCwibmFtZSI6Iui_kOe7tOeuoeeQhuWRmCIsIm1vYmlsZSI6bnVsbCwiZXh0ZXJuYWxJZCI6IjgzMzg0NDEyMTYzMzkwMTEzNDYiLCJ1ZEFjY291bnRVdWlkIjoiNzljZTkxMjE4MWI3NWRmYjQyMWY3MjdlNGZkMWQ5YjN0ZnRuM0UybGxsNCIsIm91SWQiOiI0Nzg3ODM2Mjg3ODI4NjMyOTYwIiwib3VOYW1lIjoiSURhYVMiLCJvcGVuSWQiOm51bGwsImlkcFVzZXJuYW1lIjoieXVud2VpIiwidXNlcm5hbWUiOiJ5dW53ZWkiLCJhcHBsaWNhdGlvbk5hbWUiOiLmtojpmLLnianogZTlronlhajnrqHnkIbns7vnu58iLCJlbnRlcnByaXNlSWQiOiJmeGRjIiwiaW5zdGFuY2VJZCI6ImZ4ZGMiLCJhbGl5dW5Eb21haW4iOiIiLCJleHRlbmRGaWVsZHMiOnsidGhlbWVDb2xvciI6ImdyZWVuIiwiYXBwTmFtZSI6Iua2iOmYsueJqeiBlOWuieWFqOeuoeeQhuezu-e7nyJ9LCJleHAiOjE3NDA5OTk4NTYsImp0aSI6IlBxNEVQUmRRR0I2cFVnWmYyUFY0eEEiLCJpYXQiOjE3NDA5OTkyNTYsIm5iZiI6MTc0MDk5OTE5Niwic3ViIjoieXVud2VpIiwiaXNzIjoiaHR0cDovLzEwLjExNi4xNy4zOS8iLCJhdWQiOiJmeGRjcGx1Z2luX2p3dDI1In0.GudmnAUxaBMFp_ati_fslac4bQ70iMC3cv0Smn6XUXS979bCjLhRnmIZPsB7-q6vCFQNzWgl-yUKqwGBCNfSC5aZPs9X_Hp9uaFUQZTlGCI-heV0Ck5rJzr4k6HRpZFfZx56FmWqnyvcefRdQwPkmFZlDVSjhpHmia0Q0sEET8ZbivHUb5ssA8b4UMLUVX1-qThZv_8vxEWZhkKOLcfaulQuUZDN2xx3yx0Lhvm69AxRyf9mP108s_tdrYtA1YURlX407A53RjdX-C6qTk5AgMtoF9vvlcs_E_EZ3abneUH69VaAtiBFrTA4fccFakqzDApXu5mdDAMZIUC29tnQlQ";
String tokne2="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoi5byg5LiJIiwic3ViIjoidXNlcjEyMyIsImV4cCI6MTc0MTA4ODE1MX0.guTy-TufrZ33jXMVPKiVOLfMJ2tBRHP-sk_bUjAZxQo";

        checkAndGetUsername(tokne2,publickey);
    }

    private static String checkAndGetUsername(String id_token, String publickey) throws Exception {
        //1. 初始化
        final RsaPublicJwk key = (RsaPublicJwk) Jwks.parser().build().parse(publickey);
        final RSAPublicKey rsaPublicKey = key.toKey();
        //2. 校验id_token是否合法
        Claims claims ;
        try {
            DecodedJWT decode = JWT.decode(id_token);
            System.out.println(decode);

            claims= Jwts.parser()
                    .verifyWith(rsaPublicKey)
                    .build()
                    .parseSignedClaims(id_token)
                    .getPayload();
        }catch (SignatureException e){

            //校验失败，报错，返回
            return "error";
        }

        //3. 校验id_token是否过期
        Date expirationTime = claims.getExpiration();
        if (expirationTime != null && expirationTime.before(new Date())) {

            //校验失败，报错，返回
            return "error";
        }
        //4. 注意校验id_token是否已经登陆过，防重放攻击
        //业务系统自己实现，需要校验有效期内，是否有相同的id_token已经登录
        final String jti = claims.getId();//获取token唯一标识
        //从自身缓存系统判断jti是否已经登录过
        //if(exit(jti)){
        //    model.addAttribute("error", "id_token verifySignature failed");
        //    return error;
        //}
        //save(jti);

        //5.获取到用户信息，检测用户名是否存在自己的业务系统中，isExistedUsername方法为示例实现
        String username = claims.getSubject();
        return  "";
    }
}
