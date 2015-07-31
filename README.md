# Ruler
A horizontal scrolling Ruler，it contains two different mode,timeline mode (HH:mm),and normal ruler mode;
一个横向滑动的刻度尺demo，它有两种模式，一种是timeline(时间轴格式，24小时）,一种是ruler（普通的刻度尺）

#使用方法 
在布局xml定义如下 <br>
···XML
···<br>
···<com.joey.ruler.library.Ruler···<br>
···        android:id="@+id/ruler"···<br>
···        android:layout_width="fill_parent"···<br>
···        android:layout_height="wrap_content"···<br>
···        android:layout_below="@+id/result_text"···<br>
···        android:background="#ffaaaaaa"···<br>
···        android:orientation="horizontal"···<br>
···        ruler:max_unit_count="24"···<br>
···        ruler:min_unit_size="2dp"···<br>
···        ruler:per_unit_count="10"···<br>
···        ruler:unit_visible="max"···<br>
···        ruler:ruler_mode="timeline"···<br>
···        ruler:unit_bmp_height="20dp" />
···  <br>   
max_unit_count 表示最大的单位数目 <br>
min_unit_size 表示最小单位所占的像素大小，理解为画出来的最小单位的宽度 单位时demon <br>
per_unit_count 表示最大单位包含的最小单位数<br>
unit_visible 表示刻度的可见性，刻度有三种，大、中、小，max 表示只有大刻度可见 min 表示 大刻度小刻度可见、 mid 表示大刻度中刻度小刻度都可见<br>
ruler_mode 表示刻度尺的类型，目前有两种，一种是时间轴格式timeline，一种是普通刻度尺 ruler <br>
unit_bmp_height 表示最大刻度的高度<br>


demo picture :
![demo](/shotcut.png)

