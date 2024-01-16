package top.sharehome.demo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 物种数据文件实体类
 *
 * @author AntonyCheng
 */
@TableName(value = "specie_data_file")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SpecieDataFile implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "specie_data_file_id", type = IdType.ASSIGN_ID)
    private Long specieDataFileId;

    /**
     * 文件保存路径
     */
    @TableField(value = "path")
    private String path;


    /**
     * 文件类型【1.图片，2.视频】
     */
    @TableField(value = "file_type")
    private String fileType;

    /**
     * 采集数据表id
     */
    @TableField(value = "specie_data_camera_id")
    private String specieDataCameraId;

    /**
     * 有效数据【0有效数据，1无效数据】
     */
    @TableField(value = "effective")
    private String effective;

    /**
     * 0：绘制图片；1：非绘制图片
     */
    @TableField(value = "redraw_flag")
    private String redrawFlag;

    /**
     * 设备id
     */
    @TableField(value = "device_id")
    private String deviceId;

    /**
     * 拍摄时间
     */
    @TableField(value = "photograph_time")
    private Date photographTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 删除人
     */
    @TableField(value = "delete_by")
    private String deleteBy;

    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private Date deleteTime;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableField(value = "del_flag")
    @TableLogic
    private String delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}