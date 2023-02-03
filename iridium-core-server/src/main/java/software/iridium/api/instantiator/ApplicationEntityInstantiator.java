/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package software.iridium.api.instantiator;

import java.security.NoSuchAlgorithmException;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.iridium.api.authentication.domain.ApplicationCreateRequest;
import software.iridium.api.entity.ApplicationEntity;
import software.iridium.api.entity.ApplicationTypeEntity;
import software.iridium.api.util.EncoderUtils;

@Component
public class ApplicationEntityInstantiator {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationEntityInstantiator.class);

  public static final Integer CLIENT_ID_SEED_LENGTH = 16;

  @Resource private EncoderUtils encoderUtils;

  @Transactional(propagation = Propagation.REQUIRED)
  public ApplicationEntity instantiate(
      final ApplicationCreateRequest request,
      final ApplicationTypeEntity type,
      final String tenantId) {
    final var entity = new ApplicationEntity();
    entity.setApplicationType(type);
    entity.setTenantId(tenantId);
    entity.setName(request.getName());
    try {
      entity.setClientId(encoderUtils.cryptoSecureToHex(CLIENT_ID_SEED_LENGTH));
    } catch (NoSuchAlgorithmException e) {
      logger.error("error creating client id: ", e);
      throw new RuntimeException("error creating client id for application: " + type.getId());
    }
    return entity;
  }
}