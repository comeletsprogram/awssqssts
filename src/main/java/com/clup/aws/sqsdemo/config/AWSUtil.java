package com.clup.aws.sqsdemo.config;


import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSUtil {
	
//	@Value("${et.aws.kms.key.arn}")
//    String kmsKeyArn;

    private static final Logger log = LoggerFactory.getLogger(AWSUtil.class);
    
	@Autowired
	@Qualifier("kmsMasterKeyProvider")
	private static KmsMasterKeyProvider kmsMasterKeyProviderSNS;

    public static String getDecryptedMessage(String messagePayload) {
		String decryptedMessage = "";
		final AwsCrypto crypto = new AwsCrypto();

		final CryptoResult<String, KmsMasterKey> decryptResult = crypto.decryptString(kmsMasterKeyProviderSNS,
				messagePayload);
		if (!decryptResult.getMasterKeyIds().get(0).equals("<kms key arn>")) {
			throw new IllegalStateException("Wrong key ID!");
		} else {
			decryptedMessage = decryptResult.getResult();
			
		}
		return decryptedMessage;
	}
  /*
    public String getKmsKeyArn() {
        return kmsKeyArn;
    }

    public void setKmsKeyArn(String kmsKeyArn) {
        this.kmsKeyArn = kmsKeyArn;
    }*/
}
