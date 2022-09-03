grammar ISL;
program: program_line | program_line NEWLINE program;
program_line: NEWLINE | comment | move;
comment: '#' UNICODE_STRING;
move:
	pcut_move
	| lcut_move
	| color_move
	| swap_move
	| merge_move;
pcut_move: 'cut' block point;
lcut_move: 'cut' block orientation line_number;
color_move: 'color' block color;
swap_move: 'swap' block block;
merge_move: 'merge' block block;
orientation: '[' orientation_type ']';
orientation_type: vertical | horizontal;
vertical: 'X' | 'x';
horizontal: 'Y' | 'y';
line_number: '[' NUMBER ']';
block: '[' block_id ']';
point: '[' NUMBER ',' NUMBER ']';
color: '[' NUMBER ',' NUMBER ',' NUMBER ',' NUMBER ']';
block_id: NUMBER | NUMBER '.' block_id;
NUMBER: [0-9]+;
NEWLINE: [\n];
UNICODE_STRING: [^\n]+;