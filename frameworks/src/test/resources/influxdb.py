
class LineProtocol:
    """行协议数据包"""

    def __init__(self,
                 measurement: str = None,
                 time: int = None,
                 tags: dict = dict(),
                 fields: dict = dict(),
                 ):
        self.measurement = measurement
        self.time = time
        self.tags = tags
        self.fields = fields


def parse_line(line: str):
    """
    解析行协议数据
    :line :行协议字符串
    """
    if line is None or line.strip() == '':
        raise Exception("数据不能为空")
    line_protocol = LineProtocol()
    stage = 0
    # 引号
    has_single_quote = False
    has_double_quote = False
    escape = False
    i = 0
    start_at = 0
    size = len(line)
    while i < size:
        ch = line[i: i + 1]
        if escape or ch == '\\':
            # 有转义符，跳过
            escape = not escape
        else:
            if has_single_quote or has_double_quote:
                # 有引号，需要退出引号后记录
                if has_single_quote and ch == '/':
                    has_single_quote = False
                if has_double_quote and ch == '\"':
                    has_double_quote = False
            else:
                if ch == ' ' or ch == ',':
                    # 0 =>: tag和字段的分割
                    if stage == 0:
                        line_protocol.measurement = line[start_at: i]
                        stage += 1  # tag
                    elif stage == 1:
                        _str = line[start_at: i]
                        if _str == '' or _str == ' ':
                            continue
                        splits = _str.split("=")
                        line_protocol.tags[splits[0]] = splits[1]
                    elif stage == 2:
                        _str = line[start_at: i]
                        if _str == '' or _str == ' ':
                            continue
                        splits = _str.split("=")
                        line_protocol.fields[splits[0]] = parse_value(splits[1])  # 待解析
                    elif stage == 3:
                        line_protocol.time = int(line[start_at:len(line)])
                    stage = stage + (1 if ch == ' ' else 0)
                    start_at = i + 1
                else:
                    if ch == '\'':
                        has_single_quote = True
                    if ch == '\"':
                        has_double_quote = True
        i += 1
    spaceLastIndexOf = line.rindex(' ')
    if spaceLastIndexOf >= 0:
        line_protocol.time = int(line[spaceLastIndexOf: len(line)].strip())
    else:
        raise Exception("缺少时间戳")
    return line_protocol


def parse_value(value: str):
    """
    解析值：整型、浮点数、布尔、字符串
    """
    if value.startswith('"') and value.endswith('"'):
        return value[1:len(value) - 1]
    if value.endswith('i'):
        return int(value[0:len(value) - 1])
    if value == 'true':
        return True
    if value == 'false':
        return False
    return float(value)

