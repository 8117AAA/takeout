package edu.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.takeout.entity.Category;

public interface CategoryService extends IService<Category> {
    void removeCategory(Long id);
}
