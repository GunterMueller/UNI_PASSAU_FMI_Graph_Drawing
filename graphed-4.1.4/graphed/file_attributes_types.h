typedef	struct	file_attributes	{

	enum	{
		FILE_KEY,
		FILE_NUMBER,
		FILE_FLOATNUMBER,
		FILE_STRING,
		FILE_LIST
	}
		kind;

	union	{
		struct	{
			char			*name;
			struct	file_attributes	*values;
		}
			key;

		int	number;
		double	floatnumber;

		char	*string;

		struct	file_attributes	*list;
	}
		value;

	struct	file_attributes	*next;
}
	*File_attributes;

