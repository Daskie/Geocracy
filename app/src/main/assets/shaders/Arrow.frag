#version 300 es

precision highp float;

in vec2 v2f_pos;
flat in float v2f_angle;

out vec4 out_color;

uniform float u_time;
uniform vec3 u_color;

const float k_radToDegFactor = 180.0f / 3.14159265f;
const float k_thickness = 0.025f;
const float k_invThickness = 1.0f / k_thickness;
const float k_dashLength = 4.0f;
const float k_invDashLength = 1.0f / k_dashLength;
const float k_dashPortion = 0.75f;
const float k_invDashPortion = 1.0f / k_dashPortion;
const float k_headPortion = 0.25f;
const float k_invHeadPortion = 1.0f / k_headPortion;
const float k_tailPortion = 1.0f - k_headPortion;
const float k_invTailPortion = 1.0f / k_tailPortion;
const float k_tailWidth = 0.5f;
const float k_borderWidth = 0.2f;
const float k_backHeadBorderThreshold = 1.0f - k_borderWidth * 0.75f;

void main() {
    float pos = (1.0f - v2f_pos.x) * v2f_angle;

    pos = fract(pos * k_invThickness * 0.5f * k_invDashLength + u_time);
    pos *= k_invDashPortion * float(pos < k_dashPortion);

    float revPos = 1.0f - pos;

    float head = pos * k_invHeadPortion * float(pos < k_headPortion);
    float tail = revPos * k_invTailPortion * float(revPos < k_tailPortion);

    float y = abs(v2f_pos.y);

    float arrow = float(y < head || y < tail * k_tailWidth);
    float interior = float(y < head - k_borderWidth && (head < k_backHeadBorderThreshold || y < k_tailWidth - k_borderWidth) || y < tail * k_tailWidth - k_borderWidth);
    out_color.rgb = u_color * interior;
    out_color.a = arrow;
}
