#version 300 es

in vec3 in_loc;

out vec2 v2f_loc;

void main() {
	gl_Position = vec4(in_loc.x, in_loc.y, in_loc.z, 1.0f);

	v2f_loc = in_loc.xy;

}
