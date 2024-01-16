package top.sharehome.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.sharehome.demo.model.entity.SpecieDataFile;

import java.util.List;

/**
 * 物种数据文件Mapper
 *
 * @author AntonyCheng
 */
@Mapper
public interface SpecieDataFileMapper extends BaseMapper<SpecieDataFile> {

    @Select("select  distinct path from specie_data_file   where substr(path,1,9) = '/xgadmin/' and del_flag = '0'")
    List<String> getAllPath();

}




