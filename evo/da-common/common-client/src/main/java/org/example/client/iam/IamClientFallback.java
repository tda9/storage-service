package org.example.client.iam;


import lombok.extern.slf4j.Slf4j;
import org.example.model.UserAuthority;
import org.example.model.dto.response.BasedResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IamClientFallback implements FallbackFactory<IamClient> {
    @Override
    public IamClient create(Throwable cause) {
        return new FallbackWithFactory(cause);
    }

    @Slf4j
    static class FallbackWithFactory implements IamClient {
        private final Throwable cause;

        FallbackWithFactory(Throwable cause) {
            this.cause = cause;
        }


        @Override
        public BasedResponse<UserAuthority> getUserAuthority(String username) {
            return BasedResponse.fail("Cannot get User Authority",(RuntimeException) cause);
        }

        @Override
        public BasedResponse<UserAuthority> getClientAuthority(UUID clientId) {
            return BasedResponse.fail("Cannot get Client Authority",(RuntimeException) cause);
        }

        @Override
        public BasedResponse<String> getClientToken(String clientId, String clientSecret) {
            return BasedResponse.fail("Cannot get client Token",(RuntimeException) cause);
        }
    }
}
