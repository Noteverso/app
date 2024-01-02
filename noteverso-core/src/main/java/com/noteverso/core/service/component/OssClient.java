package com.noteverso.core.service.component;

import cn.hutool.core.util.IdUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.noteverso.common.exceptions.OssException;
import com.noteverso.core.config.OssClientProperties;
import com.noteverso.core.constant.OssConstants;
import com.noteverso.core.model.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Slf4j
@Component
public class OssClient {
    private final AmazonS3 client;
    private final OssClientProperties ossClientProperties;

    public OssClient(OssClientProperties ossClientProperties) {
        this.ossClientProperties = ossClientProperties;
        try {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(ossClientProperties.getEndpoint(), "");
            AWSCredentials credentials = new BasicAWSCredentials(ossClientProperties.getAccessKey(), ossClientProperties.getAccessKeySecret());
            AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            if (OssConstants.IS_HTTPS.equals(ossClientProperties.getIsHttps())) {
                clientConfiguration.setProtocol(Protocol.HTTPS);
            } else {
                clientConfiguration.setProtocol(Protocol.HTTP);
            }

            AmazonS3ClientBuilder build = AmazonS3Client.builder()
                    .withEndpointConfiguration(endpointConfiguration)
                    .withClientConfiguration(clientConfiguration)
                    .withCredentials(credentialsProvider)
                    .disableChunkedEncoding();
           client = build.build();
        } catch (Exception e) {
            if (e instanceof OssException) {
                throw e;
            }
            throw new OssException("配置错误！请检查系统配置:[" + e.getMessage() +"]");
        }
    }

    public UploadResult upload(InputStream inputStream, String key, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(inputStream.available());
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossClientProperties.getBucketName(), key, inputStream, metadata);
            // 设置上传对象的 Acl 为私有
            putObjectRequest.setCannedAcl(CannedAccessControlList.Private);
            client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
        return UploadResult.builder().url(getUrl() + "/" + key).fileName(key).build();
    }

    public UploadResult upload(File file, String key) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossClientProperties.getBucketName(), key, file);
            // 设置上传对象的 Acl 为私有
            putObjectRequest.setCannedAcl(CannedAccessControlList.Private);
            client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
        return UploadResult.builder().url(getUrl() + "/" + key).fileName(key).build();
    }

    public void delete(String path) {
        String key = path.replace(getUrl() + "/", "");
        try {
            client.deleteObject(ossClientProperties.getBucketName(), key);
        } catch (Exception e) {
            throw new OssException("删除文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
    }

    public String getUrl() {
        return ossClientProperties.getEndpoint() + "/" + ossClientProperties.getBucketName();
    }

    /**
     * 获取私有资源的url，过期时间为second秒
     * @param objectKey 文件名称，不包含bucketName，例如：test.jpg，如果是目录，则需要加上'/'，例如：test/test.jpg，如
     * @param second 过期时间，单位为秒，最大为3600秒，最小为1秒
     */
    public String getPrivateUrl(String objectKey, Integer second) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(ossClientProperties.getBucketName(), objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(new Date(System.currentTimeMillis() + 1000L * second));
        URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    public String createPresignedPutUrl(String objectKey, Integer second) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(ossClientProperties.getBucketName(), objectKey)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(new Date(System.currentTimeMillis() + 1000L * second));
        URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    public String getKey(String prefix, String suffix) {
        String uuid = IdUtil.fastSimpleUUID();
        String path = DateFormatUtils.format(new Date(), "yyyy/MM/dd") + "/" + uuid;
        if (StringUtils.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }
        return path + suffix;
    }
}
