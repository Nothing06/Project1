#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#define LEFT_UP 1
#define LEFT_DOWN 2
#define RIGHT_UP 3
#define RIGHT_DOWN 4

int start_idx = 0;
void choose_start_pos(int n, int * dir, int * r, int * c)
{
	switch (*dir)
	{
	case 1:
	case 2:
		*r = 0; *c = 0;
		break;
	case 3:
	case 4:
		*r = n - 1; *c = 0;
		break;
	case 5:
	case 6:
		*r = 0; *c = n - 1;
		break;
	case 7:
	case 8:
		*r = n - 1; *c = n - 1;
		break;
	}
}
void print_matrix(int ** p, int n)
{
	int i = 0,j=0;
	for (i = 0; i < n; i += 1)
	{
		for(j=0;j<n;j+=1)
			printf("%02d ", p[i][j]);
		printf("\n");
	}
}
void choose_value(int n, int * menu_val, int * value, int * inc)
{
	if (*menu_val == 1)
	{
		*value = 1;
		*inc = 1;
	}
	else if (*menu_val == 2)
	{
		*value = n*n;
		*inc = -1;
	}
}
void zigzag(int ** p, int n, int dir, /*int menu_num*/  int menu_val)// menu_num은 8개의번호리스트(방향 번호), menu_val은 2개의번호리스트(값 설정 번호)
{
	int i = 0,j=0;
	int numC_in_line = 1;
	int linecnt = 0;
	int row_idx = 0;
	int col_idx = 0;
	int value = 0;
	int inc = 0;
	int loop_inc = 1;
	int c = 0;
	int pass_mid = 0;

	linecnt = (n - 1) * 2 + 1;
	choose_value(n, &menu_val, &value, &inc);
	choose_start_pos(n, &dir, &row_idx, &col_idx);

	i = 1;
	while(c<linecnt)
	{
		switch (dir)
		{
		case 1:
		case 2:
			if (dir == 1) // 오른쪽방향
			{
				int k = 0;
				while (k < i)
				{
				//	printf("row_idx: %d col_idx: %d\n", row_idx, col_idx);
					p[row_idx][col_idx] = value;
					
					value += inc;
					if (i > 1 && k < i - 1)
					{
						if (i % 2 == 0)
						{
							row_idx += 1;
							col_idx -= 1;
						}
						else
						{
							row_idx -= 1;
							col_idx += 1;
						}
					}
					k += 1;
				}
				if (c < linecnt - 1)
				{
					if (i < linecnt / 2 + 1)
					{
						if (pass_mid == 0)
						{
							if (i % 2 == 1)
							{
								col_idx += 1;
							}
							else
							{
								row_idx += 1;
							}
						}
						else
						{
							if (i % 2 == 1)
							{
								row_idx += 1;
							}
							else
							{
								col_idx += 1;
							}
						}
					}
					else if (i == linecnt / 2 + 1)
					{
						pass_mid = 1;
						if (n % 2 == 1)
						{
							row_idx += 1;
						//	printf("HH");
						}
						else
							col_idx += 1;
					}
				}

			}
			else if (dir == 2) // 아래쪽방향
			{
				int k = 0;
				while (k < i)
				{
				//	printf("row_idx: %d col_idx: %d\n", row_idx, col_idx);
					p[row_idx][col_idx] = value;

					value += inc;
					if (i > 1 && k<i - 1)
					{
							if (i % 2 == 0)
							{
								row_idx -= 1;
								col_idx += 1;
							}
							else
							{
								row_idx += 1;
								col_idx -= 1;
							}
					}
					k += 1;
				}// while문 탈출
				if (c < linecnt - 1)
				{
					if (i < linecnt / 2 + 1)
					{
						if (pass_mid == 0)
						{
							if (i % 2 == 0)
							{
								col_idx += 1;
							}
							else
							{
								row_idx += 1;
							}
						}
						else
						{
							if (i % 2 == 1)
							{
								col_idx += 1;
							}
							else
							{
								row_idx += 1;
							}
						}
					}
					else if (i == linecnt / 2 + 1)
					{

						if (n % 2 == 1)
						{
							col_idx += 1;
							//	printf("HH");
						}
						else
							row_idx += 1;
						pass_mid = 1;
					}
				}
			}
			break;
		case 3:
		case 4:
			if (dir == 3) // 오른쪽방향
			{
			int k = 0;
				while (k < i)
				{
				//	printf("row_idx: %d col_idx: %d\n", row_idx, col_idx);
					p[row_idx][col_idx] = value;

					value += inc;
					if (i > 1 && k<i - 1)
					{
						
							if (i % 2 == 0)
							{

								row_idx -= 1;
								col_idx -= 1;

							}
							else
							{
								row_idx += 1;
								col_idx += 1;
							}
					}
					k += 1;
				}// while문 탈출
				if (c < linecnt - 1)
				{
					if (i < linecnt / 2 + 1)
					{
						if (pass_mid == 0)
						{
							if (i % 2 == 0)
							{
								row_idx -= 1;
							}
							else
							{
								col_idx += 1;
							}
						}
						else
						{
							if (i % 2 == 1)
							{
								row_idx -= 1;
							}
							else
							{
								col_idx += 1;
							}
						}
					}
					else if (i == linecnt / 2 + 1)
					{
						
						if (n % 2 == 1)
						{
							row_idx -= 1;
							//	printf("HH");
						}
						else
							col_idx += 1;
						pass_mid = 1;
					}
				}
			}
			else if (dir == 4) // 위쪽방향
			{
				int k = 0;
				while (k < i)
				{
		//			printf("row_idx: %d col_idx: %d\n", row_idx, col_idx);
					p[row_idx][col_idx] = value;

					value += inc;
					if (i > 1 && k<i - 1)
					{
							if (i % 2 == 0)
							{
								row_idx += 1;
								col_idx += 1;
							}
							else
							{
								row_idx -= 1;
								col_idx -= 1;
							}
					}
					k += 1;
				}// while문 탈출
				if (c < linecnt - 1)
				{
					if (i < linecnt / 2 + 1)
					{
						if (pass_mid == 0)
						{
							if (i % 2 == 0)
							{
								col_idx += 1;
							}
							else
							{
								row_idx -= 1;
							}
						}
						else
						{
							if (i % 2 == 1)
							{
								col_idx += 1;
							}
							else
							{
								row_idx -= 1;
							}
						}
					}
					else if (i == linecnt / 2 + 1)
					{

						if (n % 2 == 1)
						{
							col_idx += 1;
							//	printf("HH");
						}
						else
							row_idx -= 1;
						pass_mid = 1;
					}
				}
			}
			break;
		case 5:
		case 6:
			if (dir == 5) // 왼쪽방향
			{
				int k = 0;
				while (k < i)
				{
		//			printf("row_idx: %d col_idx: %d\n", row_idx, col_idx);
					p[row_idx][col_idx] = value;

					value += inc;
					if (i > 1 && k<i - 1)
					{
						if (i % 2 == 0)
						{

							row_idx += 1;
							col_idx += 1;

						}
						else
						{

							row_idx -= 1;
							col_idx -= 1;

						}


					}
					k += 1;
				}// while문 탈출
				if (c < linecnt - 1)
				{
					if (i < linecnt / 2 + 1)
					{
						if (pass_mid == 0)
						{
							if (i % 2 == 0)
							{
								row_idx += 1;
							}
							else
							{
								col_idx -= 1;
							}
						}
						else
						{
							if (i % 2 == 1)
							{
								row_idx += 1;
							}
							else
							{
								col_idx -= 1;
							}
						}
					}
					else if (i == linecnt / 2 + 1)
					{

						if (n % 2 == 1)
						{
							row_idx += 1;
							//	printf("HH");
						}
						else
							col_idx -= 1;
						pass_mid = 1;
					}
				}
			}
			else if (dir == 6) // 아래쪽방향
			{
				int k = 0;
				while (k < i)
				{
		//			printf("row_idx: %d col_idx: %d\n", row_idx, col_idx);
					p[row_idx][col_idx] = value;

					value += inc;
					if (i > 1 && k<i - 1)
					{
						if (i % 2 == 0)
						{
							row_idx -= 1;
							col_idx -= 1;
						}
						else
						{

							row_idx += 1;
							col_idx += 1;

						}


					}
					k += 1;
				}// while문 탈출
				if (c < linecnt - 1)
				{
					if (i < linecnt / 2 + 1)
					{
						if (pass_mid == 0)
						{
							if (i % 2 == 0)
							{
								col_idx -= 1;
							}
							else
							{
								row_idx += 1;
							}
						}
						else
						{
							if (i % 2 == 1)
							{
								col_idx -= 1;
							}
							else
							{
								row_idx += 1;
							}
						}
					}
					else if (i == linecnt / 2 + 1)
					{

						if (n % 2 == 1)
						{
							col_idx -= 1;
							//	printf("HH");
						}
						else
							row_idx += 1;
						pass_mid = 1;
					}
				}
			}
			break;
		case 7:
		case 8:
			if (dir == 7) // 왼쪽방향
			{
				int k = 0;
				while (k < i)
				{
		//			printf("row_idx: %d col_idx: %d\n", row_idx, col_idx);
					p[row_idx][col_idx] = value;

					value += inc;
					if (i > 1 && k<i - 1)
					{
						if (i % 2 == 0)
						{
							row_idx -= 1;
							col_idx += 1;
						}
						else
						{

							row_idx += 1;
							col_idx -= 1;

						}


					}
					k += 1;
				}// while문 탈출
				if (c < linecnt - 1)
				{
					if (i < linecnt / 2 + 1)
					{
						if (pass_mid == 0)
						{
							if (i % 2 == 0)
							{
								row_idx -= 1;
							}
							else
							{
								col_idx -= 1;
							}
						}
						else
						{
							if (i % 2 == 1)
							{
								row_idx -= 1;
							}
							else
							{
								col_idx -= 1;
							}
						}
					}
					else if (i == linecnt / 2 + 1)
					{
						if (n % 2 == 1)
						{
							row_idx -= 1;
						}
						else
							col_idx -= 1;
						pass_mid = 1;
					}
				}
			}
			else if (dir == 8) // 위족방향
			{
				int k = 0;
				while (k < i)
				{
		//			printf("row_idx: %d col_idx: %d\n", row_idx, col_idx);
					p[row_idx][col_idx] = value;

					value += inc;
					if (i > 1 && k<i - 1)
					{
						if (i % 2 == 0)
						{
							row_idx += 1;
							col_idx -= 1;
						}
						else
						{
							row_idx -= 1;
							col_idx += 1;
						}
					}
					k += 1;
				}// while문 탈출
				if (c < linecnt - 1)
				{
					if (i < linecnt / 2 + 1)
					{
						if (pass_mid == 0)
						{
							if (i % 2 == 0)
							{
								col_idx -= 1;
							}
							else
							{
								row_idx -= 1;
							}
						}
						else
						{
							if (i % 2 == 1)
							{
								col_idx -= 1;
							}
							else
							{
								row_idx -= 1;
							}
						}
					}
					else if (i == linecnt / 2 + 1)
					{
						if (n % 2 == 1)
						{
							col_idx -= 1;
						}
						else
							row_idx -= 1;
						pass_mid = 1;
					}
				}
			}
			break;
		}
		i += loop_inc;
		c += 1;
		if (i == linecnt / 2 + 1)
		{
			loop_inc *= -1;
		}
	}
}
int** init_2Arr(int n)
{
	int i;
	int **arr;
	arr = (int**)malloc(sizeof(int*)*n);
	for (i = 0; i < n; i += 1)
	{
		arr[i] = (int*)malloc(sizeof(int)*n);
		memset(arr[i], 0, sizeof(int)*n);
	}
	return arr;
}
void free_matrix(int ** p, int row)
{
	int i = 0;
	for (i = 0; i < row; i += 1)
	{
		free(p[i]);
	}
	free(p);
}
void show_dir_info()
{
	printf("1. 좌상단에서 오른쪽 방향 시작\n");
	printf("2. 좌상단에서 아래쪽 방향 시작\n");
	printf("3. 좌하단에서 오른쪽 방향 시작\n");
	printf("4. 좌하단에서   위쪽 방향 시작\n");
	printf("5. 우상단에서   왼쪽 방향 시작\n");
	printf("6. 우상단에서 아래쪽 방향 시작\n");
	printf("7. 우하단에서   왼쪽 방향 시작\n");
	printf("8. 우하단에서   위쪽 방향 시작\n");
}
int main(int argc, char * argv[])
{
	int n = 0;
	int ** p;
	int final_dir = 0;
	int value = 0;
	int dir = 0;
	char question[100] = { 0, };

	do {
		printf("2이상 9이하의 숫자 입력: ");
		scanf("%d", &n);
		getchar();
	} while (n < 2 || n>9);
	p = init_2Arr( n);

	//show_dir_info();
	do {
		printf("시작점을 선택하시오(Up-Left: 1, Low-Left: 2, Up-Right: 3, Low-Right: 4): ");
		scanf("%d", &start_idx);
		getchar();
	} while (start_idx < 0 || start_idx > 4);

	if (start_idx == 1)
	{
		sprintf(question, "방향을 선택하시오(South: 1, East: 2): ");
	}
	else if (start_idx == 2)
	{
		sprintf(question, "방향을 선택하시오(North: 1, East: 2): ");
	}
	else if (start_idx == 3)
	{
		sprintf(question, "방향을 선택하시오(South: 1, West: 2): ");
	}
	else if (start_idx == 4)
	{
		sprintf(question, "방향을 선택하시오(North: 1, West: 2): ");
	}


	do {
		printf(question);
		scanf("%d", &dir);
		getchar();
	} while (dir < 0 || dir > 2);
	
	do {
		printf("증감방법을 선택하시오(증가: 1, 감소: 2): ");
		scanf("%d", &value);
		getchar();
	} while (value < 1 || value > 2);
	
	switch (start_idx)
	{
	case LEFT_UP:
		if (dir == 1)
			final_dir = 2;
		else if (dir == 2)
			final_dir = 1;
		break;
	case LEFT_DOWN:
		if (dir == 1)
			final_dir = 4;
		else if (dir == 2)
			final_dir = 3;
		break;
	case RIGHT_UP:
		if (dir == 1)
			final_dir = 6;
		else if (dir == 2)
			final_dir = 5;
		break;
	case RIGHT_DOWN:
		if (dir == 1)
			final_dir = 8;
		else if (dir == 2)
			final_dir = 7;
		break;
	}
	//value = 1;
	zigzag(p, n, final_dir, value );
	print_matrix(p, n);
	return 0;
}
