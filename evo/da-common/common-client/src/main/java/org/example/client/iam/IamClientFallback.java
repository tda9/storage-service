//package org.example.client.iam;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.example.model.UserAuthority;
//import org.springframework.cloud.openfeign.FallbackFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//public class IamClientFallback implements FallbackFactory<IamClient> {
//    @Override
//    public IamClient create(Throwable cause) {
//        return new FallbackWithFactory(cause);
//    }
//
//    @Slf4j
//    static class FallbackWithFactory implements IamClient {
//        private final Throwable cause;
//
//        FallbackWithFactory(Throwable cause) {
//            this.cause = cause;
//        }
//
//        @Override
//        public ResponseEntity<UserAuthority> getUserAuthority(UUID userId) {
//            if (cause instanceof ForwardInnerAlertException) {
//                return ResponseEntity.fail((RuntimeException) cause);
//            }
//            return Response.fail(
//                    new ResponseException(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR));
//        }
//
//        @Override
//        public Response<UserAuthority> getUserAuthority(String username) {
//            if (cause instanceof ForwardInnerAlertException) {
//                return Response.fail((RuntimeException) cause);
//            }
//            return Response.fail(
//                    new ResponseException(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR));
//        }
//    }
//}
