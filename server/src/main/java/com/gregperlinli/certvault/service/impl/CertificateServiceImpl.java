package com.gregperlinli.certvault.service.impl;

import com.gregperlinli.certvault.domain.entities.Certificate;
import com.gregperlinli.certvault.mapper.CertificateMapper;
import com.gregperlinli.certvault.service.interfaces.ICertificateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * SSL证书 服务实现类
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
@Service
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements ICertificateService {

}
