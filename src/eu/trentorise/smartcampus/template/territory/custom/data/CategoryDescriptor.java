package eu.trentorise.smartcampus.template.territory.custom.data;

public final class CategoryDescriptor {
	public int map_icon;
	public int thumbnail;
	public String category;
	public int description;

	public CategoryDescriptor(int map_icon, int thumbnail, String category, int description) {
		super();
		this.map_icon = map_icon;
		this.thumbnail = thumbnail;
		this.category = category;
		this.description = description;
	}
}
