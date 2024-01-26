import React from 'react';

const JsonTable = ({ node }) => {

    const renderTableHeader = ()=> {
        return <thead>
                <th>Name:</th>
                <th>Wert:</th>
              </thead>
    }

  const renderTableData = () => {

    return Object.keys(node.scratch()).map((item) => {
        var scratch = node.scratch()
      return (
        <tr key={item}>
            <td key={item}>{item}</td>
            <td key={item}>{scratch[item]}</td>
        </tr>
      );
    });
  };

  return (
    <table border={1}>
        {renderTableHeader()}
      <tbody>{renderTableData()}</tbody>
    </table>
  );
};

export default JsonTable;
