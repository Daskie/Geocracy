#version 300 es

layout (location = 0) in vec3 in_pos;

out vec3 v2f_texCoord;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;

void main() {
    v2f_texCoord = in_pos;

	gl_Position = u_projMat * vec4((u_viewMat * vec4(in_pos, 0.0f)).xyz, 1.0f);
	gl_Position.z = gl_Position.w;
}
