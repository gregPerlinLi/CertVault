package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.CaBindingDTO;
import com.gregperlinli.certvault.domain.dto.CaInfoDTO;
import com.gregperlinli.certvault.domain.dto.PageDTO;
import com.gregperlinli.certvault.domain.entities.CaBinding;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * Allocate CA Users Service
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
public interface ICaBindingService extends IService<CaBinding> {

    /**
     * Create a new CA binding
     *
     * @param caBindingDTO CA binding information
     * @return true if the CA binding is created successfully
     */
    Boolean newBinding(CaBindingDTO caBindingDTO);

    /**
     * Delete a CA binding
     *
     * @param caBindingDTO CA binding information
     * @return true if the CA binding is deleted successfully
     */
    Boolean deleteBinding(CaBindingDTO caBindingDTO);

}
