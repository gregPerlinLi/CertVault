package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.CaBindingDTO;
import com.gregperlinli.certvault.domain.dto.CaInfoDTO;
import com.gregperlinli.certvault.domain.dto.PageDTO;
import com.gregperlinli.certvault.domain.entities.CaBinding;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
     * Create multiple CA bindings
     *
     * @param caBindingDTOs CA binding information
     * @return true if the CA bindings are created successfully
     * @throws Exception if the CA binding information is invalid
     */
    Boolean newBindings(List<CaBindingDTO> caBindingDTOs) throws Exception;

    /**
     * Delete a CA binding
     *
     * @param caBindingDTO CA binding information
     * @return true if the CA binding is deleted successfully
     */
    Boolean deleteBinding(CaBindingDTO caBindingDTO);

    /**
     * Delete multiple CA bindings
     *
     * @param caBindingDTOs CA binding information
     * @return true if the CA bindings are deleted successfully
     * @throws Exception if the CA binding information is invalid
     */
    Boolean deleteBindings(List<CaBindingDTO> caBindingDTOs) throws Exception;


    /**
     * Count all ca certificates bound to user
     *
     * @param username username
     * @return ca certificates count
     */
    Long countBoundCa(String username);

}
