package my.cinemax.app.free.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import my.cinemax.app.free.entity.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    
    @Query("SELECT * FROM categories ORDER BY id ASC")
    LiveData<List<Category>> getAllCategories();
    
    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(int id);
    
    @Query("SELECT COUNT(*) FROM categories")
    int getCategoriesCount();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<Category> categories);
    
    @Update
    void updateCategory(Category category);
    
    @Delete
    void deleteCategory(Category category);
    
    @Query("DELETE FROM categories")
    void deleteAllCategories();
}